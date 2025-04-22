import React from "react";
import { Typography, Box } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import DynamicReusableTable from "../utils/DynamicReusableTable";
import config from "../../config/AwsServiceColumnNameConfig";
import getStatusColor from "../utils/statusColorUtils";
import { fetchASGInstances } from "../../services/awsServiceApis";

const ASG = ({ accountNumber }) => {
  const {
    data = [],
    isLoading,
    isFetching,
    isError,
    error,
  } = useQuery({
    queryKey: ["asg-instances", accountNumber],
    queryFn: () => fetchASGInstances(accountNumber),
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });

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

  return (
    <DynamicReusableTable
      columns={columns}
      data={Array.isArray(data) ? data : []}
      isLoading={isLoading || isFetching}
      isError={isError}
      error={error}
      renderCell={renderCell}
      enableFilters
      getRowId={(row) => row.id}
      headerColor="#1e3a8a"
    />
  );
};

export default ASG;
