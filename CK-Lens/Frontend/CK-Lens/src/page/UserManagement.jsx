import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Container, Typography, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import FilterListIcon from "@mui/icons-material/FilterList";
import AddIcon from "@mui/icons-material/Add";
import { useSelector } from "react-redux";
import { useInfiniteQuery } from "@tanstack/react-query";
import UserFilterToggle from "../components/utils/UserFilterToggle";
import DynamicReusableTable from "../components/utils/DynamicReusableTable";
import config from "../config/userManagementTableColumns";
import { fetchUsers } from "../services/userManagementServiceApis";

//import LoadingScreen from "../../LoadingScreen/LoadingScreen";
const UserManagementDashboard = () => {
  const navigate = useNavigate();
  const [filter, setFilter] = useState("active");
  const open = useSelector((state) => state.sidebar.open);
  const { dashboardPermissions } = useSelector((state) => state.auth);
  const columns = config.columns;

  const hasEditPermission = (permissions, dashboardName) => {
    if (!permissions) return false;
    const dashboard = permissions.find(
      (perm) => perm.dashboard === dashboardName
    );
    return dashboard && dashboard.permissionType === "EDIT";
  };

  const canEdit = hasEditPermission(dashboardPermissions, "USER_MANAGEMENT");

  const {
    data,
    fetchNextPage,
    isLoading,
    hasNextPage,
    isFetchingNextPage,
    isError,
    error,
  } = useInfiniteQuery({
    queryKey: ["users"],
    queryFn: ({ pageParam = 0 }) => fetchUsers(pageParam),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.hasNextPage ? allPages.length : undefined;
    },
  });

  const allUsers = data?.pages.flatMap((page) => page.users) || [];

  const filteredUsers =
    filter === "active" ? allUsers.filter((user) => user.active) : allUsers;

  const handleFilterChange = (_event, newFilter) => {
    if (newFilter !== null) {
      setFilter(newFilter);
    }
  };

  const handleScroll = (event) => {
    if (!event.target) return;

    const { scrollHeight, scrollTop, clientHeight } = event.target;
    const threshold = 100;
    const isNearBottom = scrollHeight - scrollTop - clientHeight < threshold;

    if (isNearBottom && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
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

        {/* Actions */}
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
            onClick={() => setFilter("active")}
          >
            Reset Filters
          </Button>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <UserFilterToggle
              filter={filter}
              onChange={handleFilterChange}
              activeCount={allUsers.filter((u) => u.active).length}
              allCount={allUsers.length}
            />
          </Box>
        </Box>

        {/* Table */}
        {isLoading && allUsers.length === 0 ? (
          <Typography>Fetching Users...</Typography>
        ) : isError ? (
          <Typography color="error">
            Error Fetching Users:{" "}
            {error instanceof Error ? error.message : "Unknown error"}
          </Typography>
        ) : (
          <DynamicReusableTable
            columns={columns}
            data={filteredUsers}
            isLoading={isLoading && allUsers.length === 0}
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
