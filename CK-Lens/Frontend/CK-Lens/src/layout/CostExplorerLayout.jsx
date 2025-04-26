import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  Select,
  MenuItem,
  Typography,
  FormControl,
  InputLabel,
} from "@mui/material";
import { ChevronDown, SlidersHorizontal } from "lucide-react";
import CostExplorerDataWrapper from "../components/utils/CostExplorerDataWrapper";
import { useQuery } from "@tanstack/react-query";
import { fetchDisplayNames } from "../services/costExplorer";

const CostExplorerLayout = () => {
  const [toggleFilter, setToggleFilter] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState("");
  const [orderedGroups, setOrderedGroups] = useState([]);

  const { data: displayNames = [], isLoading } = useQuery({
    queryKey: ["display-names"],
    queryFn: fetchDisplayNames,
  });

  useEffect(() => {
    if (displayNames.length > 0 && orderedGroups.length === 0) {
      setOrderedGroups(displayNames);
    }
  }, [displayNames, orderedGroups]);

  const updateOrderWithSelected = (selectedDisplayName) => {
    setOrderedGroups((prev) => {
      const selected = prev.find((g) => g.displayName === selectedDisplayName);
      const rest = prev.filter((g) => g.displayName !== selectedDisplayName);
      return [selected, ...rest];
    });
    setSelectedGroup(selectedDisplayName);
  };
  const visibleGroups = orderedGroups.slice(0, 6);
  const moreGroups = orderedGroups.slice(6);

  const handleMoreSelect = (e) => {
    const selectedFieldName = e.target.value;
    const selected = orderedGroups.find(
      (item) => item.fieldName === selectedFieldName
    );
    updateOrderWithSelected(selected.displayName);
  };

  console.log("Selected:->", selectedGroup);
  return (
    <Box sx={{ p: 2 }}>
      <Box
        display="flex"
        justifyContent={"space-between"}
        mb={2}
      >
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            width: "full",
          }}
        >
          <Typography
            sx={{
              mr: 1,
              whiteSpace: "nowrap",
            }}
            variant="subtitle1"
          >
            Group By:
          </Typography>
          <Box
            sx={{
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              gap: 1.5,
              flexWrap: "wrap",
            }}
          >
            {visibleGroups.map((item, index) => (
              <React.Fragment key={item.fieldName}>
                <Button
                  variant={index === 0 ? "contained" : "outlined"}
                  onClick={() => updateOrderWithSelected(item.displayName)}
                >
                  {item.displayName}
                </Button>
                {index === 0 && visibleGroups.length > 1 && (
                  <Box
                    sx={{
                      borderRight: "1px solid lightgray",
                      height: "40px",
                      alignSelf: "center",
                      mx: 1,
                    }}
                  />
                )}
              </React.Fragment>
            ))}
          </Box>
          <Box sx={{ mr: 1 }}></Box>

          {moreGroups.length > 0 && (
            <Box
              sx={{
                width: "auto",
              }}
            >
              <FormControl
                size="small"
                fullWidth
                sx={{ m: 1, minWidth: 80 }}
              >
                <InputLabel>More</InputLabel>
                <Select
                  onChange={handleMoreSelect}
                  IconComponent={ChevronDown}
                >
                  {moreGroups.map((item) => (
                    <MenuItem
                      key={item.fieldName}
                      value={item.fieldName}
                    >
                      {item.displayName}{" "}
                      {/* Displaying the displayName for the user */}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>
          )}
        </Box>
        <Button
          onClick={() => {
            setToggleFilter(!toggleFilter);
          }}
        >
          <SlidersHorizontal />
        </Button>
      </Box>

      <Box
        sx={{
          display: "flax",
          flexDirection: "row",
        }}
      >
        <CostExplorerDataWrapper
          selectedOption={selectedGroup}
          toggleFilter={toggleFilter}
          filters={displayNames}
        />
      </Box>
    </Box>
  );
};
export default CostExplorerLayout;
