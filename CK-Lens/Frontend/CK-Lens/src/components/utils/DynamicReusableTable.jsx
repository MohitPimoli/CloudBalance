import React, { useState, useMemo } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  IconButton,
  Box,
  CircularProgress,
  Stack,
  TextField,
  InputAdornment,
} from "@mui/material";
import { ShieldAlert, Ban, Filter, XCircle } from "lucide-react";

const DynamicReusableTable = ({
  columns,
  data = [],
  isLoading,
  isError,
  error,
  onScroll,
  enableFilters = false,
  filterableColumns = [],
  getRowId = (row) => row.id,
  renderCell,
  onRowClick,
  headBgColor = "#1e3a8a",
  headTextColor = "white",
  footerData = null,
}) => {
  const [filters, setFilters] = useState({});
  const [filterVisible, setFilterVisible] = useState({});

  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const toggleFilterVisibility = (key) => {
    setFilterVisible((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));
  };

  const filteredData = useMemo(() => {
    return data.filter((item) =>
      Object.entries(filters).every(([key, value]) =>
        value
          ? String(item[key] ?? "")
              .toLowerCase()
              .includes(value.toLowerCase())
          : true
      )
    );
  }, [data, filters]);

  return (
    <TableContainer
      component={Paper}
      sx={{ maxHeight: 700, overflowY: "auto" }}
      onScroll={onScroll}
    >
      <Table stickyHeader>
        <TableHead>
          <TableRow>
            {columns.map(({ label, key }) => {
              const isFilterable = filterableColumns.includes(key);
              return (
                <TableCell
                  key={key}
                  sx={{
                    backgroundColor: headBgColor,
                    color: headTextColor,
                    position: "sticky",
                    top: 0,
                    zIndex: 2,
                    minWidth: 140,
                  }}
                >
                  <Box
                    display="flex"
                    flexDirection="column"
                  >
                    <Box
                      display="flex"
                      justifyContent="space-between"
                      alignItems="center"
                    >
                      <Typography
                        variant="subtitle2"
                        sx={{ color: "white", fontWeight: 500 }}
                      >
                        {label}
                      </Typography>
                      {enableFilters && isFilterable && (
                        <IconButton
                          size="small"
                          sx={{ color: "white", p: 0.5 }}
                          onClick={() => toggleFilterVisibility(key)}
                        >
                          <Filter size={16} />
                        </IconButton>
                      )}
                    </Box>
                    {enableFilters && isFilterable && filterVisible[key] && (
                      <TextField
                        onBlur={() =>
                          setFilterVisible((prev) => ({
                            ...prev,
                            [key]: false,
                          }))
                        }
                        variant="standard"
                        value={filters[key] || ""}
                        onChange={(e) =>
                          handleFilterChange(key, e.target.value)
                        }
                        placeholder="Search..."
                        fullWidth
                        sx={{
                          mt: 1,
                          backgroundColor: "white",
                          borderRadius: 1,
                        }}
                        InputProps={{
                          endAdornment: filters[key] ? (
                            <InputAdornment position="end">
                              <IconButton
                                size="small"
                                onClick={() => handleFilterChange(key, "")}
                              >
                                <XCircle size={16} />
                              </IconButton>
                            </InputAdornment>
                          ) : null,
                        }}
                      />
                    )}
                  </Box>
                </TableCell>
              );
            })}
          </TableRow>
        </TableHead>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell
                colSpan={columns.length}
                align="center"
              >
                <Stack
                  direction="row"
                  justifyContent="center"
                  alignItems="center"
                  spacing={1}
                >
                  <CircularProgress size={20} />
                  <Typography variant="body2">Fetching data...</Typography>
                </Stack>
              </TableCell>
            </TableRow>
          ) : isError ? (
            <TableRow>
              <TableCell
                colSpan={columns.length}
                align="center"
              >
                <Stack
                  direction="column"
                  alignItems="center"
                  spacing={1}
                >
                  <Ban color="red" />
                  <Typography
                    color="error"
                    variant="body2"
                  >
                    Error fetching data:{" "}
                    {error || error?.message || "Unknown error"}
                  </Typography>
                </Stack>
              </TableCell>
            </TableRow>
          ) : filteredData.length === 0 ? (
            <TableRow>
              <TableCell
                colSpan={columns.length}
                align="center"
              >
                <Stack
                  direction="column"
                  alignItems="center"
                  spacing={1}
                >
                  <ShieldAlert color="#FFBF00" />
                  <Typography variant="body2">Data not found...</Typography>
                </Stack>
              </TableCell>
            </TableRow>
          ) : (
            filteredData.map((row, index) => (
              <TableRow
                key={getRowId(row) || `${row.id}-${index}`}
                onClick={() => onRowClick?.(row)}
                sx={{ cursor: onRowClick ? "pointer" : "default" }}
              >
                {columns.map(({ key }) => (
                  <TableCell key={key}>
                    {renderCell ? renderCell(row, key) : row[key]}
                  </TableCell>
                ))}
              </TableRow>
            ))
          )}
        </TableBody>
        {footerData && (
          <TableBody>
            <TableRow
              sx={{
                position: "sticky",
                bottom: 0,
                backgroundColor: headBgColor,
                zIndex: 1,
              }}
            >
              {columns.map(({ key }) => (
                <TableCell key={key}>
                  <Typography
                    variant="subtitle2"
                    sx={{ fontWeight: "bold", color: headTextColor }}
                  >
                    {footerData[key] !== undefined
                      ? typeof footerData[key] === "number"
                        ? `$${footerData[key].toFixed(2)}`
                        : footerData[key]
                      : ""}
                  </Typography>
                </TableCell>
              ))}
            </TableRow>
          </TableBody>
        )}
      </Table>
    </TableContainer>
  );
};

export default DynamicReusableTable;
