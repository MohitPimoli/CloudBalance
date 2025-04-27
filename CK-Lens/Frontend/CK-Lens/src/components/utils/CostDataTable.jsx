import React, { useMemo } from "react";
import { Paper, Typography, Box, Chip, useTheme } from "@mui/material";
import DynamicReusableTable from "./DynamicReusableTable";

const CostDataTable = (data) => {
  const theme = useTheme();

  const processedData = useMemo(() => {
    if (!data?.data) return { rowData: [], total: 0 };

    const instanceMap = new Map();
    let totalCost = 0;

    data.data.forEach((item) => {
      const name = item.groupBy || "Other";

      if (!instanceMap.has(name)) {
        instanceMap.set(name, 0);
      }

      const cost = Math.abs(item.cost);
      instanceMap.set(name, instanceMap.get(name) + cost);
      totalCost += cost;
    });

    const rowData = Array.from(instanceMap.entries())
      .map(([service, cost]) => ({ service, cost }))
      .sort((a, b) => b.cost - a.cost);

    return { rowData, total: totalCost };
  }, [data]);

  const rowsWithPercentage = processedData.rowData.map((row) => ({
    ...row,
    percentage: (row.cost / processedData.total) * 100,
  }));

  const columns = [
    { key: "service", label: "Service" },
    { key: "cost", label: "Cost" },
    { key: "percentage", label: "% of Total" },
  ];

  const formatCurrency = (value) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  const renderCell = (row, key) => {
    const index = rowsWithPercentage.findIndex(
      (r) => r.service === row.service
    );

    if (key === "service") {
      return (
        <Box sx={{ display: "flex", alignItems: "center" }}>
          {index < 3 && (
            <Chip
              label={index + 1}
              size="small"
              color="primary"
              sx={{
                mr: 1,
                fontWeight: "bold",
                width: 24,
                height: 24,
              }}
            />
          )}
          <Typography
            variant="body2"
            fontWeight={index < 3 ? 600 : 400}
          >
            {row.service}
          </Typography>
        </Box>
      );
    }

    if (key === "cost") {
      return (
        <Typography
          variant="body2"
          fontWeight={index < 3 ? 600 : 400}
          color={index < 3 ? theme.palette.primary.main : "inherit"}
        >
          {formatCurrency(row.cost)}
        </Typography>
      );
    }

    if (key === "percentage") {
      return (
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
          }}
        >
          <Box
            sx={{
              width: 100,
              mr: 1,
              height: 8,
              bgcolor: theme.palette.grey[200],
              borderRadius: 1,
              position: "relative",
              overflow: "hidden",
            }}
          >
            <Box
              sx={{
                position: "absolute",
                left: 0,
                top: 0,
                height: "100%",
                width: `${row.percentage}%`,
                maxWidth: "100%",
                bgcolor:
                  index < 3
                    ? theme.palette.primary.main
                    : theme.palette.primary.light,
              }}
            />
          </Box>
          <Typography
            variant="body2"
            sx={{ minWidth: 45 }}
          >
            {row.percentage.toFixed(1)}%
          </Typography>
        </Box>
      );
    }

    return row[key];
  };

  if (!processedData.rowData.length) {
    return (
      <Paper
        elevation={2}
        sx={{
          p: 3,
          textAlign: "center",
          borderRadius: 2,
        }}
      >
        <Typography
          variant="body1"
          color="text.secondary"
        >
          No cost data available for the selected period.
        </Typography>
      </Paper>
    );
  }

  return (
    <Box>
      <DynamicReusableTable
        columns={columns}
        data={rowsWithPercentage}
        renderCell={renderCell}
        footerData={{
          service: "Total",
          cost: formatCurrency(processedData.total),
          percentage: "100%",
        }}
      />
    </Box>
  );
};

export default CostDataTable;
