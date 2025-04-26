import React, { useMemo } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Box,
  Chip,
  useTheme,
} from "@mui/material";

const CostDataTable = (data) => {
  const theme = useTheme();

  // Group data by instance type and calculate total cost
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

    // Convert to array and sort by cost (descending)
    const rowData = Array.from(instanceMap.entries())
      .map(([service, cost]) => ({ service, cost }))
      .sort((a, b) => b.cost - a.cost);

    return { rowData, total: totalCost };
  }, [data]);

  // Format currency values
  const formatCurrency = (value) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  };

  // Handle empty state
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

  // Calculate percentage of total for each service
  const rowsWithPercentage = processedData.rowData.map((row) => ({
    ...row,
    percentage: (row.cost / processedData.total) * 100,
  }));

  return (
    <Paper
      elevation={2}
      sx={{ borderRadius: 2, overflow: "hidden" }}
    >
      <TableContainer sx={{ maxHeight: 440 }}>
        <Table
          stickyHeader
          sx={{ minWidth: 650 }}
          aria-label="cost data table"
        >
          <TableHead>
            <TableRow sx={{ backgroundColor: theme.palette.grey[100] }}>
              <TableCell
                sx={{
                  fontWeight: "bold",
                  color: theme.palette.text.primary,
                  fontSize: "0.875rem",
                  backgroundColor: theme.palette.grey[100],
                }}
              >
                Service
              </TableCell>
              <TableCell
                align="right"
                sx={{
                  fontWeight: "bold",
                  color: theme.palette.text.primary,
                  fontSize: "0.875rem",
                  backgroundColor: theme.palette.grey[100],
                }}
              >
                Cost
              </TableCell>
              <TableCell
                align="right"
                sx={{
                  fontWeight: "bold",
                  color: theme.palette.text.primary,
                  fontSize: "0.875rem",
                  backgroundColor: theme.palette.grey[100],
                }}
              >
                % of Total
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rowsWithPercentage.map((row, index) => (
              <TableRow
                key={row.service}
                sx={{
                  "&:nth-of-type(odd)": {
                    backgroundColor: theme.palette.action.hover,
                  },
                  "&:last-child td, &:last-child th": { border: 0 },
                  borderLeft:
                    index < 3
                      ? `4px solid ${theme.palette.primary.main}`
                      : "none",
                }}
              >
                <TableCell
                  component="th"
                  scope="row"
                >
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
                </TableCell>
                <TableCell align="right">
                  <Typography
                    variant="body2"
                    fontWeight={index < 3 ? 600 : 400}
                    color={index < 3 ? theme.palette.primary.main : "inherit"}
                  >
                    {formatCurrency(row.cost)}
                  </Typography>
                </TableCell>
                <TableCell align="right">
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
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Total row as a separate fixed component */}
      <Box
        sx={{
          borderTop: `2px solid ${theme.palette.divider}`,
          backgroundColor: theme.palette.primary.light,
          p: 1.5,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography
          variant="subtitle2"
          fontWeight="bold"
          sx={{
            pl: 2,
            color: theme.palette.common.white,
          }}
        >
          Total
        </Typography>
        <Typography
          variant="subtitle2"
          fontWeight="bold"
          sx={{
            minWidth: 120,
            textAlign: "right",
            color: theme.palette.common.white,
          }}
        >
          {formatCurrency(processedData.total)}
        </Typography>
        <Typography
          variant="subtitle2"
          fontWeight="bold"
          sx={{
            minWidth: 80,
            textAlign: "right",
            color: theme.palette.common.white,
          }}
        >
          100%
        </Typography>
      </Box>
    </Paper>
  );
};

export default CostDataTable;
