import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Container, Typography, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import FilterListIcon from "@mui/icons-material/FilterList";
import AddIcon from "@mui/icons-material/Add";
import { useSelector } from "react-redux";
import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import UserFilterToggle from "../components/utils/UserFilterToggle";
import DynamicReusableTable from "../components/utils/DynamicReusableTable";
import config from "../config/userManagementTableColumns";
import {
  fetchUsers,
  fetchUsersStatus,
} from "../services/userManagementServiceApis";
import LoadingScreen from "../page/LoadingScreen";

const UserManagementDashboard = () => {
  const navigate = useNavigate();
  const [filter, setFilter] = useState("active");
  const open = useSelector((state) => state.sidebar.open);
  const { dashboardPermissions } = useSelector((state) => state.auth);
  const columns = config.columns;
  const [clearFilters, setClearFilters] = useState(false);

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

  const {
    data: statusData = {},
    isLoading: statusLoading,
    error: statusError,
  } = useQuery({
    queryKey: ["user-status"],
    queryFn: fetchUsersStatus,
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

  const handleResetFilters = () => {
    setClearFilters(true);
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
        pr: 8,
      }}
    >
      <Container
        maxWidth="fullWidth"
        disableGutters
        sx={{
          mb: 5,
          ml: open ? "15px" : "5px",
          transition: "margin-left 0.3s ease",
          height: "100%",
          display: "flex",
          flexDirection: "column",
          mr: 1,
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
            onClick={() => {
              setFilter("active");
              setClearFilters(true);
            }}
          >
            Reset Filters
          </Button>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <UserFilterToggle
              filter={filter}
              onChange={handleFilterChange}
              activeCount={statusData.active}
              allCount={statusData.all}
            />
          </Box>
        </Box>

        {/* Table */}
        {isLoading && allUsers.length === 0 ? (
          <LoadingScreen message="Fetching users..." />
        ) : isError ? (
          <Typography color="error">
            Error Fetching Users:{" "}
            {error instanceof Error ? error.message : "Unknown error"}
          </Typography>
        ) : (
          <DynamicReusableTable
            columns={columns}
            data={filteredUsers}
            clearFilters={clearFilters}
            onFiltersCleared={() => setClearFilters(false)}
            isLoading={isLoading && allUsers.length === 0}
            isError={isError}
            error={error}
            enableFilters
            renderCell={renderCell}
            onScroll={handleScroll}
            filterableColumns={["firstName", "roleName"]}
          />
        )}
      </Container>
    </Box>
  );
};

export default UserManagementDashboard;
