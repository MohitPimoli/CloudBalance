import React from "react";
import { Box, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        height: "100vh",
        background: "linear-gradient(135deg, #0f1c4d, #1e3c72)",
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
        variant="h1"
        sx={{
          fontSize: { xs: "4rem", md: "8rem" },
          fontWeight: 900,
          color: "#ff4081", // vibrant pink
          textShadow: "2px 2px 8px rgba(255,255,255,0.2)",
        }}
      >
        404
      </Typography>
      <Typography
        variant="h5"
        sx={{
          mb: 2,
          color: "#90caf9", // light blue
        }}
      >
        Oops! The page you're looking for doesn't exist.
      </Typography>
      <Button
        variant="contained"
        size="large"
        onClick={() => navigate("/")}
        sx={{
          backgroundColor: "#1976d2", // dark blue
          "&:hover": {
            backgroundColor: "#115293",
          },
          color: "white",
          mt: 2,
          borderRadius: "20px",
          px: 4,
          py: 1,
          boxShadow: "0 4px 12px rgba(255, 64, 129, 0.4)",
        }}
      >
        Go to Home
      </Button>
    </Box>
  );
};

export default NotFound;
