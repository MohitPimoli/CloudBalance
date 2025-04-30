import React, { useEffect } from "react";
import { Typography, Box } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import DynamicReusableTable from "../utils/DynamicReusableTable";
import config from "../../config/AwsServiceColumnNameConfig";
import getStatusColor from "../utils/statusColorUtils";
import { fetchASGInstances } from "../../services/awsServiceApis";

const ASG = ({ accountNumber, setSnackbar }) => {
  const { data, isLoading, isFetching, isError, error } = useQuery({
    queryKey: ["asg-instances", accountNumber],
    queryFn: () => fetchASGInstances(accountNumber),
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });

  if (data?.status === 204) {
    setSnackbar({
      open: true,
      message: "No ASG instances found.",
      severity: "info",
    });
    setTimeout(() => {
      setSnackbar({
        open: false,
        message: "",
        severity: "info",
      });
    }, 4000);
    return null;
  }
  const columns = config.ASG.columns;

  const renderCell = (row, key) => {
    if (key === "status") {
      const { label, color } = getStatusColor(row.status);
      return (
        <Box
          display="flex"
          alignItems="center"
        >
          <Box
            sx={{
              width: 8,
              height: 8,
              borderRadius: "50%",
              mr: 1,
              backgroundColor: color,
            }}
          />
          <Typography
            variant="body2"
            sx={{ fontWeight: 500, color }}
          >
            {label}
          </Typography>
        </Box>
      );
    }

    return row[key];
  };

  useEffect(() => {
    if (isError) {
      setSnackbar({
        open: true,
        message: error?.response?.data?.message || "Please try again later.",
        severity: "error",
      });
    }
  }, [isError, error]);

  return (
    <DynamicReusableTable
      columns={columns}
      data={Array.isArray(data?.data) ? data?.data : []}
      isLoading={isLoading || isFetching}
      isError={isError}
      error={!!error ? error?.response.data.message : "Please try again later."}
      renderCell={renderCell}
      enableFilters
      getRowId={(row) => row.id}
      headerColor="#1e3a8a"
      filterableColumns={[
        "id",
        "name",
        "region",
        "desiredCapacity",
        "minSize",
        "maxSize",
        "status",
      ]}
    />
  );
};

export default ASG;
