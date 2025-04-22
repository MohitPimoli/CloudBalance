import React from "react";
import { Box, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

const InternalServerError = () => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        height: "100vh",
        background: "linear-gradient(135deg, #4b1d3f, #7b4397)",
        color: "white",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        padding: 3,
      }}
    >
      <Typography
        variant="h2"
        sx={{
          fontWeight: 800,
          color: "#ff6e40",
          textShadow: "2px 2px 6px rgba(255,255,255,0.2)",
        }}
      >
        500 Internal Server Error
      </Typography>
      <Typography
        variant="body1"
        sx={{ mt: 2, color: "#f48fb1" }}
      >
        Oops! Something went wrong on our end.
      </Typography>
      <Button
        variant="contained"
        onClick={() => navigate("/")}
        sx={{
          mt: 3,
          backgroundColor: "#d84315",
          color: "white",
          borderRadius: "20px",
          px: 4,
          py: 1,
          boxShadow: "0 4px 12px rgba(255, 110, 64, 0.4)",
          "&:hover": { backgroundColor: "#bf360c" },
        }}
      >
        Return to Home
      </Button>
    </Box>
  );
};

export default InternalServerError;
