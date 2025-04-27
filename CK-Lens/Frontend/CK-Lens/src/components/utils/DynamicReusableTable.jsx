import React, { useState, useMemo, useEffect, useRef } from "react";
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
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";
import { ShieldAlert, Ban } from "lucide-react";
import { Filter } from "lucide-react";

const MENU_PROPS = {
  PaperProps: {
    style: {
      maxHeight: 200,
      width: 200,
    },
  },
};

const DynamicReusableTable = ({
  columns,
  data = [],
  isLoading,
  isError,
  error,
  onScroll,
  enableFilters = false,
  filterableDropdownColumns = [],
  getRowId = (row) => row.id,
  renderCell,
  onRowClick,
  headBgColor = "#1e3a8a",
  headTextColor = "white",
  footerData = null,
}) => {
  const [filters, setFilters] = useState({});
  const [filterVisible, setFilterVisible] = useState({});
  const [autoOpenFilterKey, setAutoOpenFilterKey] = useState(null);
  const dropdownRefs = useRef({});
  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const toggleFilterVisibility = (key) => {
    setFilterVisible((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));
    setAutoOpenFilterKey(key);
  };

  const getUniqueValuesForColumn = (key) => {
    const values = new Set(
      data.map((item) => item[key]).filter((v) => v !== undefined && v !== null)
    );
    return Array.from(values);
  };

  const filteredData = useMemo(() => {
    return data.filter((item) =>
      Object.entries(filters).every(
        ([key, value]) => value === "" || item[key] === value
      )
    );
  }, [data, filters]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      const isClickInsideAny = Object.values(dropdownRefs.current).some((ref) =>
        ref?.contains(event.target)
      );
      if (!isClickInsideAny) {
        setFilterVisible({});
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

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
              const isDropdown = filterableDropdownColumns.includes(key);
              const showFilter = enableFilters && filterVisible[key];
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
                  ref={(el) => {
                    if (el) dropdownRefs.current[key] = el;
                  }}
                >
                  {!showFilter ? (
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
                      {enableFilters && isDropdown && (
                        <IconButton
                          size="small"
                          sx={{ color: "white", p: 0.5, ml: 1 }}
                          onClick={() => toggleFilterVisibility(key)}
                        >
                          <Filter size={16} />
                        </IconButton>
                      )}
                    </Box>
                  ) : (
                    <FormControl
                      sx={{ ml: 2 }}
                      fullWidth
                      size="small"
                      Checkbox
                    >
                      <InputLabel>{label}</InputLabel>
                      <Select
                        sx={{ borderRadius: 2 }}
                        open={autoOpenFilterKey === key}
                        onOpen={() => setAutoOpenFilterKey(key)}
                        onClose={() => setAutoOpenFilterKey(null)}
                        value={filters[key] || ""}
                        onChange={(e) => {
                          handleFilterChange(key, e.target.value);
                          setFilterVisible({});
                        }}
                        label={label}
                        MenuProps={MENU_PROPS}
                      >
                        <MenuItem value="">All</MenuItem>
                        {getUniqueValuesForColumn(key).map((val) => (
                          <MenuItem
                            key={val}
                            value={val}
                          >
                            {val}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
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
                    Error fetching data: {error?.message || "Unknown error"}
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
          <tfoot>
            <TableRow
              sx={{
                backgroundColor: headBgColor,
              }}
            >
              {columns.map(({ key }, idx) => (
                <TableCell key={key}>
                  <Typography
                    variant="subtitle2"
                    sx={{
                      fontWeight: "bold",
                      color: headTextColor,
                    }}
                  >
                    {footerData[key] !== undefined ? footerData[key] : ""}
                  </Typography>
                </TableCell>
              ))}
            </TableRow>
          </tfoot>
        )}
      </Table>
    </TableContainer>
  );
};

export default DynamicReusableTable;
