import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Popover,
  Checkbox,
  FormControlLabel,
  CircularProgress,
  Typography,
  Paper,
  Divider,
  Chip,
  alpha,
  Tooltip,
  TextField,
  InputAdornment,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { fetchDistinctValues } from "../../services/costExplorerApis";
import { FilterList, Check, Close, Search } from "@mui/icons-material";
import { useDispatch, useSelector } from "react-redux";
import {
  clearFilters,
  updateFieldFilters,
} from "../../redux/actions/filterActions";

const DynamicFilterFields = ({ fields = [], onApplyFilters }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [activeField, setActiveField] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredValues, setFilteredValues] = useState([]);

  const dispatch = useDispatch();
  const selectedValues = useSelector((state) => state.filters);

  const open = Boolean(anchorEl);

  const { data: distinctValues, isFetching } = useQuery({
    queryKey: ["distinct-values", activeField],
    queryFn: () => fetchDistinctValues(activeField),
    enabled: !!activeField,
    staleTime: 1000 * 60 * 5,
  });

  // Update filtered values when distinct values change or search term changes
  useEffect(() => {
    if (!distinctValues) {
      setFilteredValues([]);
      return;
    }

    if (!searchTerm) {
      setFilteredValues(distinctValues);
      return;
    }

    const lowerSearchTerm = searchTerm.toLowerCase();
    const filtered = distinctValues.filter(
      (value) => value && value.toLowerCase().includes(lowerSearchTerm)
    );
    setFilteredValues(filtered);
  }, [distinctValues, searchTerm]);

  const handleClick = (event, field) => {
    setAnchorEl(event.currentTarget);
    setActiveField(field.fieldName);
    // Reset search when opening a filter
    setSearchTerm("");
  };

  const handleCheckboxChange = (value) => {
    const current = selectedValues[activeField] || [];
    let updated = current.includes(value)
      ? current.filter((v) => v !== value)
      : [...current, value];

    dispatch(updateFieldFilters(activeField, updated));
  };

  const handleApply = () => {
    onApplyFilters?.(selectedValues);
    setAnchorEl(null);
    setActiveField(null);
    setSearchTerm("");
  };

  const handleClearFilter = (fieldName) => {
    dispatch(updateFieldFilters(fieldName, []));
    onApplyFilters?.(selectedValues);
  };

  const handleClearAll = () => {
    dispatch(clearFilters());
    onApplyFilters?.({});
  };

  const handleClosePopover = () => {
    setAnchorEl(null);
    setActiveField(null);
    setSearchTerm("");
  };

  const getActiveFieldDisplayName = () => {
    const field = fields.find((f) => f.fieldName === activeField);
    return field?.displayName || activeField;
  };

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  // Select/deselect all filtered values
  const handleSelectAll = (select) => {
    const current = selectedValues[activeField] || [];

    if (select) {
      // Get all values that aren't already selected
      const valuesToAdd = filteredValues.filter(
        (value) => !current.includes(value)
      );

      if (valuesToAdd.length > 0) {
        dispatch(updateFieldFilters(activeField, [...current, ...valuesToAdd]));
      }
    } else {
      // Deselect only the filtered values
      const updatedValues = current.filter(
        (value) => !filteredValues.includes(value)
      );
      dispatch(updateFieldFilters(activeField, updatedValues));
    }
  };

  // Calculate if all filtered items are selected
  const allFilteredSelected =
    filteredValues.length > 0 &&
    filteredValues.every((value) =>
      selectedValues[activeField]?.includes(value)
    );

  return (
    <Paper
      elevation={2}
      sx={{
        p: 2,
        borderRadius: 2,
        backgroundColor: "background.paper",
      }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 2,
        }}
      >
        <Typography
          variant="subtitle1"
          fontWeight="bold"
          sx={{ display: "flex", alignItems: "center" }}
        >
          <FilterList sx={{ mr: 1 }} />
          Filters
        </Typography>

        {Object.keys(selectedValues).length > 0 && (
          <Button
            size="small"
            color="error"
            onClick={handleClearAll}
            sx={{ textTransform: "none", fontWeight: "bold" }}
          >
            Clear All
          </Button>
        )}
      </Box>

      <Divider sx={{ mb: 2 }} />

      {/* Filter buttons */}
      <Box
        display="flex"
        flexDirection="column"
        gap={1.5}
      >
        {fields.map((field) => {
          const isSelected = selectedValues[field.fieldName]?.length > 0;
          const selectedCount = selectedValues[field.fieldName]?.length || 0;

          return (
            <Box
              key={field.fieldName}
              sx={{ position: "relative" }}
            >
              <Button
                variant={isSelected ? "contained" : "outlined"}
                color={isSelected ? "primary" : "inherit"}
                onClick={(e) => handleClick(e, field)}
                fullWidth
                sx={{
                  justifyContent: "space-between",
                  pl: 2,
                  pr: 2,
                  py: 1,
                  textTransform: "none",
                  borderRadius: 1.5,
                  borderWidth: isSelected ? 0 : 1,
                  "&:hover": {
                    backgroundColor: isSelected
                      ? alpha("#1976d2", 0.9)
                      : alpha("#e0e0e0", 0.5),
                  },
                  transition: "all 0.2s ease",
                }}
              >
                <Typography
                  variant="body2"
                  sx={{
                    fontWeight: isSelected ? 600 : 400,
                    color: isSelected ? "white" : "text.primary",
                    textAlign: "left",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    whiteSpace: "nowrap",
                    maxWidth: "80%",
                  }}
                >
                  {field.displayName}
                </Typography>

                {isSelected ? (
                  <Chip
                    label={selectedCount}
                    size="small"
                    sx={{
                      backgroundColor: "white",
                      color: "primary.main",
                      height: 20,
                      minWidth: 28,
                      fontWeight: "bold",
                    }}
                  />
                ) : (
                  <FilterList
                    fontSize="small"
                    color="action"
                  />
                )}
              </Button>

              {isSelected && (
                <Tooltip title="Clear filter">
                  <Close
                    sx={{
                      position: "absolute",
                      right: -10,
                      top: -10,
                      fontSize: 18,
                      color: "error.main",
                      bgcolor: "background.paper",
                      borderRadius: "50%",
                      p: 0.2,
                      border: "1px solid",
                      borderColor: "error.light",
                      cursor: "pointer",
                      "&:hover": {
                        bgcolor: "error.light",
                        color: "white",
                      },
                    }}
                    onClick={() => handleClearFilter(field.fieldName)}
                  />
                </Tooltip>
              )}
            </Box>
          );
        })}
      </Box>

      {/* Popover for filter options */}
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClosePopover}
        anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
        transformOrigin={{ vertical: "top", horizontal: "left" }}
        PaperProps={{
          elevation: 3,
          sx: {
            borderRadius: 2,
            overflow: "hidden",
          },
        }}
      >
        <Box sx={{ width: 280 }}>
          <Box
            sx={{
              bgcolor: "primary.main",
              color: "white",
              p: 1.5,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <Typography
              variant="subtitle2"
              fontWeight="bold"
            >
              {getActiveFieldDisplayName()}
            </Typography>
            <Close
              fontSize="small"
              sx={{ cursor: "pointer" }}
              onClick={handleClosePopover}
            />
          </Box>

          {/* Search field */}
          <Box
            px={2}
            pt={1.5}
            pb={1}
          >
            <TextField
              fullWidth
              size="small"
              placeholder="Search values..."
              value={searchTerm}
              onChange={handleSearchChange}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search
                      fontSize="small"
                      color="action"
                    />
                  </InputAdornment>
                ),
                endAdornment: searchTerm ? (
                  <InputAdornment position="end">
                    <Tooltip title="Clear search">
                      <Close
                        fontSize="small"
                        sx={{ cursor: "pointer" }}
                        onClick={() => setSearchTerm("")}
                      />
                    </Tooltip>
                  </InputAdornment>
                ) : null,
                sx: {
                  borderRadius: 1.5,
                  bgcolor: "background.paper",
                },
              }}
            />
          </Box>

          {/* Select/Deselect All */}
          {!isFetching && filteredValues.length > 0 && (
            <Box
              px={2}
              pb={1}
            >
              <FormControlLabel
                control={
                  <Checkbox
                    checked={allFilteredSelected}
                    indeterminate={
                      selectedValues[activeField]?.some((value) =>
                        filteredValues.includes(value)
                      ) && !allFilteredSelected
                    }
                    onChange={(e) => handleSelectAll(e.target.checked)}
                    color="primary"
                  />
                }
                label={
                  <Typography
                    variant="body2"
                    fontWeight="medium"
                  >
                    {allFilteredSelected ? "Deselect all" : "Select all"} (
                    {filteredValues.length})
                  </Typography>
                }
              />
              <Divider sx={{ mt: 1 }} />
            </Box>
          )}

          {/* List of filter options */}
          <Box
            p={2}
            pt={1}
            maxHeight={300}
            overflow="auto"
            display="flex"
            flexDirection="column"
          >
            {isFetching ? (
              <Box
                display="flex"
                justifyContent="center"
                p={3}
              >
                <CircularProgress size={30} />
              </Box>
            ) : filteredValues?.length ? (
              <Box
                display="flex"
                flexDirection="column"
                gap={0.5}
              >
                {filteredValues.map((value) => (
                  <FormControlLabel
                    key={value}
                    control={
                      <Checkbox
                        checked={
                          selectedValues[activeField]?.includes(value) || false
                        }
                        onChange={() => handleCheckboxChange(value)}
                        color="primary"
                      />
                    }
                    label={
                      <Tooltip title={value || "(Empty)"}>
                        <Typography
                          variant="body2"
                          sx={{
                            maxWidth: 180,
                            overflow: "hidden",
                            textOverflow: "ellipsis",
                            whiteSpace: "nowrap",
                          }}
                        >
                          {value || "(Empty)"}
                        </Typography>
                      </Tooltip>
                    }
                    sx={{
                      borderRadius: 1,
                      py: 0.5,
                      px: 1,
                      "&:hover": {
                        bgcolor: alpha("#e0e0e0", 0.3),
                      },
                    }}
                  />
                ))}
              </Box>
            ) : searchTerm ? (
              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ p: 2, textAlign: "center" }}
              >
                No matches found for "{searchTerm}".
              </Typography>
            ) : (
              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ p: 2, textAlign: "center" }}
              >
                No options available.
              </Typography>
            )}
          </Box>

          <Divider />

          {/* Action buttons */}
          <Box
            p={1.5}
            display="flex"
            justifyContent="space-between"
            gap={1}
          >
            <Button
              variant="outlined"
              size="small"
              onClick={handleClosePopover}
              sx={{ flex: 1 }}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              size="small"
              onClick={handleApply}
              startIcon={<Check fontSize="small" />}
              disabled={isFetching}
              sx={{ flex: 2 }}
            >
              Apply Filter
            </Button>
          </Box>
        </Box>
      </Popover>
    </Paper>
  );
};

export default DynamicFilterFields;
