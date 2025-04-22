import React from "react";
import { Button, Box } from "@mui/material";

const OnboardingNavigationController = ({
  onNext,
  onBack,
  onCancel,
  isFirstStep,
  disableNext,
  backLabel,
  nextLabel,
}) => (
  <Box
    display="flex"
    justifyContent="space-between"
    sx={{
      mb: 4,
      mx: 4,
    }}
  >
    <Button
      onClick={onCancel}
      sx={{
        border: "1px solid #0a3ca2",
        width: "6vw",
        color: "#0a3ca2",
        backgroundColor: "white",
        "&:hover": {
          backgroundColor: "#0a3ca2",
          color: "white",
        },
      }}
    >
      Cancel
    </Button>
    <Box
      sx={{
        width: "auto",
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-between",
        gap: 1,
      }}
    >
      {!isFirstStep && (
        <Button
          sx={{
            border: "1px solid #0a3ca2",
            width: "auto",
            color: "#0a3ca2",
            backgroundColor: "white",
            "&:hover": {
              backgroundColor: "#0a3ca2",
              color: "white",
            },
          }}
          onClick={onBack}
        >
          {backLabel}
        </Button>
      )}

      <Button
        variant="contained"
        onClick={onNext}
        disabled={disableNext}
        sx={{
          display: "flex",
          border: "1px solid #0a3ca2",
          width: "auto",
          color: "white",
          backgroundColor: "#0a3ca2",
          "&.Mui-disabled": {
            backgroundColor: "#ccc",
            borderColor: "#ccc",
            color: "#666",
          },
        }}
      >
        {nextLabel}
      </Button>
    </Box>
  </Box>
);

export default OnboardingNavigationController;
