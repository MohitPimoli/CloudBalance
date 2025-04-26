import React, { useState } from "react";
import {
  Box,
  Button,
  Popover,
  Checkbox,
  FormControlLabel,
  CircularProgress,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { fetchDistinctValues } from "../../services/costExplorer";

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
      const updated = current.includes(value)
        ? current.filter((v) => v !== value)
        : [...current, value];
      return { ...prev, [activeField]: updated };
    });
  };

  const handleApply = () => {
    if (activeField && selectedValues[activeField]) {
      onApplyFilters?.(selectedValues);
    }
    setAnchorEl(null);
    setActiveField(null);
  };

  return (
    <Box
      display="flex"
      flexDirection="column"
      gap={2}
    >
      {/* Level 1 list - filter buttons */}
      {fields.map((field) => {
        const isSelected = selectedValues[field.fieldName]?.length > 0;
        return (
          <Button
            key={field.fieldName}
            variant={isSelected ? "contained" : "outlined"}
            color={isSelected ? "primary" : "default"}
            onClick={(e) => handleClick(e, field)}
          >
            {field.displayName}
          </Button>
        );
      })}

      {/* Level 2 list - inside popover */}
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={() => setAnchorEl(null)}
        anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
      >
        <Box
          p={2}
          minWidth={250}
          display="flex"
          flexDirection="column"
        >
          {isFetching ? (
            <CircularProgress size={24} />
          ) : distinctValues?.length ? (
            <>
              <Box
                display="flex"
                flexDirection="column"
              >
                {distinctValues.map((value) => (
                  <FormControlLabel
                    key={value}
                    control={
                      <Checkbox
                        checked={
                          selectedValues[activeField]?.includes(value) || false
                        }
                        onChange={() => handleCheckboxChange(value)}
                      />
                    }
                    label={value}
                  />
                ))}
              </Box>
              <Box mt={2}>
                <Button
                  variant="contained"
                  onClick={handleApply}
                  fullWidth
                >
                  Apply
                </Button>
              </Box>
            </>
          ) : (
            <Typography variant="body2">No options available.</Typography>
          )}
        </Box>
      </Popover>
    </Box>
  );
};

export default DynamicFilterFields;
