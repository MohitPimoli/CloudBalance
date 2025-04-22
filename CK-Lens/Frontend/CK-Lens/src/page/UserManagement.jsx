import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Container, Typography, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import FilterListIcon from "@mui/icons-material/FilterList";
import AddIcon from "@mui/icons-material/Add";
import { useSelector } from "react-redux";
import { useQuery } from "@tanstack/react-query";
import UserFilterToggle from "../components/utils/UserFilterToggle";
import DynamicReusableTable from "../components/utils/DynamicReusableTable";
import config from "../config/userManagementTableColumns";
import { fetchUsers } from "../services/userManagementServiceApis";

//import LoadingScreen from "../../LoadingScreen/LoadingScreen";

const UserManagementDashboard = () => {
  const navigate = useNavigate();
  const [filter, setFilter] = useState("active");
  const open = useSelector((state) => state.sidebar.open);
  const [hasNextPage, setHasNextPage] = useState(false);
  const [page, setPage] = useState(0);
  const { dashboardPermissions } = useSelector((state) => state.auth);
  const [rawUsers, setRawUsers] = useState([]);
  const columns = config.columns;

  const hasEditPermission = (permissions, dashboardName) => {
    if (!permissions) return false;
    const dashboard = permissions.find(
      (perm) => perm.dashboard === dashboardName
    );
    return dashboard && dashboard.permissionType === "EDIT";
  };

  const canEdit = hasEditPermission(dashboardPermissions, "USER_MANAGEMENT");

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["users", page],
    queryFn: () => fetchUsers(page),
    keepPreviousData: true,
  });

  useEffect(() => {
    if (data) {
      setHasNextPage(data.hasNextPage);
      setRawUsers((prev) => {
        const existingIds = new Set(prev.map((u) => u.id));
        const newUsers = data.users.filter((u) => !existingIds.has(u.id));
        return page === 0 ? newUsers : [...prev, ...newUsers];
      });
    }
  }, [data, page]);

  const filteredUsers =
    filter === "active"
      ? rawUsers.filter((user) => user.active === true)
      : rawUsers;

  const handleFilterChange = (_event, newFilter) => {
    if (newFilter !== null) {
      setFilter(newFilter);
      setPage(0);
    }
  };

  const handleScroll = (event) => {
    if (!event.target) return;

    const { scrollHeight, scrollTop, clientHeight } = event.target;
    const threshold = 100;
    const isNearBottom = scrollHeight - scrollTop - clientHeight < threshold;

    if (isNearBottom && hasNextPage && !isLoading) {
      setPage((prevPage) => prevPage + 1);
    }
  };

  const renderCell = (row, key) => {
    if (key === "active") {
      return row.active ? "YES" : "NO";
    }

    if (key === "actions") {
      return (
        <IconButton
          color="primary"
          size="small"
          onClick={() => navigate(`/user-management/modify/${row.id}`)}
          disabled={!canEdit}
        >
          <EditIcon />
        </IconButton>
      );
    }

    return row[key];
  };

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        maxWidth: "100%",
        mt: 8,
        overflow: "hidden",
        ml: 2,
      }}
    >
      <Container
        maxWidth="xl"
        disableGutters
        sx={{
          mb: 5,
          ml: open ? "15px" : "80px",
          transition: "margin-left 0.3s ease",
          height: "100%",
          display: "flex",
          flexDirection: "column",
        }}
      >
        {/* Title and Actions */}
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
          mb={2}
        >
          <Typography
            variant="h5"
            fontWeight="bold"
          >
            Users
          </Typography>
        </Box>
        {/* Actions Row */}
        <Box
          display="flex"
          flexDirection={{ xs: "column", sm: "row" }}
          alignItems={{ xs: "stretch", sm: "center" }}
          mb={2}
          gap={2}
          flexWrap="wrap"
        >
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate("/add-new-user")}
            disabled={!canEdit}
          >
            Add New User
          </Button>
          <Box sx={{ borderRight: "1px solid lightgray", height: "40px" }} />
          <Button
            variant="outlined"
            startIcon={<FilterListIcon />}
            onClick={() => {
              setFilter("active");
            }}
          >
            Reset Filters
          </Button>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <UserFilterToggle
              filter={filter}
              onChange={handleFilterChange}
              activeCount={rawUsers.filter((u) => u.active).length}
              allCount={rawUsers.length}
            />
          </Box>
        </Box>
        {/* User Table */}
        {isLoading && rawUsers.length === 0 ? (
          // <LoadingScreen message="Fetching Users..." />
          <Typography>Fetching Users...</Typography>
        ) : isError ? (
          <Typography color="error">
            Error Fetching Users:{" "}
            {error instanceof Error ? error.message : "Unknown error"}
          </Typography>
        ) : (
          <DynamicReusableTable
            columns={columns}
            data={Array.isArray(filteredUsers) ? filteredUsers : []}
            isLoading={isLoading && rawUsers.length === 0}
            isError={isError}
            error={error}
            enableFilters
            renderCell={renderCell}
            onScroll={handleScroll}
            filterableDropdownColumns={["firstName", "roleName"]}
          />
        )}
      </Container>
    </Box>
  );
};

export default UserManagementDashboard;
