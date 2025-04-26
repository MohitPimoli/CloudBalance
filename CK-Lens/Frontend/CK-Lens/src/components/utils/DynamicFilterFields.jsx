import React, { useState } from "react";
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
  Badge,
  alpha,
  Tooltip,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { fetchDistinctValues } from "../../services/costExplorerApis";
import { FilterList, Check, Close } from "@mui/icons-material";


const DynamicFilterFields = ({ fields = [], onApplyFilters }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [activeField, setActiveField] = useState(null);
  const [selectedValues, setSelectedValues] = useState({});

  const open = Boolean(anchorEl);

  const { data: distinctValues, isFetching } = useQuery({
    queryKey: ["distinct-values", activeField],
    queryFn: () => fetchDistinctValues(activeField),
    enabled: !!activeField,
    staleTime: 1000 * 60 * 5,
  });

  const handleClick = (event, field) => {
    setAnchorEl(event.currentTarget);
    setActiveField(field.fieldName);
  };

  const handleCheckboxChange = (value) => {
    setSelectedValues((prev) => {
      const current = prev[activeField] || [];
      let updated = current.includes(value)
        ? current.filter((v) => v !== value)
        : [...current, value];

      if (updated.length === 0) {
        const { [activeField]: _, ...rest } = prev;
        return rest;
      }

      return { ...prev, [activeField]: updated };
    });
  };

  const handleApply = () => {
    onApplyFilters?.(selectedValues);
    setAnchorEl(null);
    setActiveField(null);
  };

  const handleClearFilter = (fieldName) => {
    setSelectedValues((prev) => {
      const { [fieldName]: _, ...rest } = prev;
      return rest;
    });
    onApplyFilters?.(selectedValues);
  };

  const getActiveFieldDisplayName = () => {
    const field = fields.find((f) => f.fieldName === activeField);
    return field?.displayName || activeField;
  };

  return (
    <Paper
      elevation={2}
      sx={{
        p: 2,
        borderRadius: 2,
        backgroundColor: "background.paper",
      }}
    >
      <Typography
        variant="subtitle1"
        fontWeight="bold"
        sx={{ mb: 2, display: "flex", alignItems: "center" }}
      >
        <FilterList sx={{ mr: 1 }} />
        Filters
      </Typography>

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

      {/* Popover content */}
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={() => setAnchorEl(null)}
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
              onClick={() => setAnchorEl(null)}
            />
          </Box>

          <Box
            p={2}
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
            ) : distinctValues?.length ? (
              <>
                <Box
                  display="flex"
                  flexDirection="column"
                  gap={0.5}
                >
                  {distinctValues.map((value) => (
                    <FormControlLabel
                      key={value}
                      control={
                        <Checkbox
                          checked={
                            selectedValues[activeField]?.includes(value) ||
                            false
                          }
                          onChange={() => handleCheckboxChange(value)}
                          color="primary"
                        />
                      }
                      label={
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
              </>
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

          <Box
            p={1.5}
            display="flex"
            justifyContent="space-between"
            gap={1}
          >
            <Button
              variant="outlined"
              size="small"
              onClick={() => setAnchorEl(null)}
              sx={{ flex: 1 }}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              size="small"
              onClick={handleApply}
              startIcon={<Check fontSize="small" />}
              disabled={isFetching || !distinctValues?.length}
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
