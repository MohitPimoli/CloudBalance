import React from "react";
import { ToggleButtonGroup, ToggleButton } from "@mui/material";

const UserFilterToggle = ({ filter, onChange, activeCount, allCount }) => {
  return (
    <ToggleButtonGroup
      value={filter}
      exclusive
      onChange={onChange}
      aria-label="user filter"
      sx={{
        borderRadius: "30px",
        border: "1px solid #1976d2",
        overflow: "hidden",
        "& .MuiToggleButton-root": {
          border: "none",
          borderRadius: 0,
          padding: "6px 20px",
          fontWeight: "bold",
          color: "#1976d2",
          backgroundColor: "#fff",
          textTransform: "none",
          transition: "all 0.3s ease-in-out",
          "&.Mui-selected": {
            backgroundColor: "#1976d2",
            color: "#fff",
          },
          "&:hover": {
            backgroundColor: "#e3f2fd",
          },
        },
        "& .MuiToggleButtonGroup-grouped:first-of-type": {
          borderTopLeftRadius: "30px",
          borderBottomLeftRadius: "30px",
        },
        "& .MuiToggleButtonGroup-grouped:last-of-type": {
          borderTopRightRadius: "30px",
          borderBottomRightRadius: "30px",
        },
      }}
    >
      <ToggleButton value="active">Active ({activeCount})</ToggleButton>
      <ToggleButton value="all">All ({allCount})</ToggleButton>
    </ToggleButtonGroup>
  );
};

export default UserFilterToggle;
