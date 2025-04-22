import React from "react";
import { Box, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

const Forbidden = () => {
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
        variant="h2"
        sx={{
          fontWeight: 800,
          color: "#ff4081",
          textShadow: "2px 2px 6px rgba(255,255,255,0.2)",
        }}
      >
        403 Forbidden
      </Typography>
      <Typography
        variant="body1"
        sx={{ mt: 2, color: "#90caf9" }}
      >
        You don't have permission to access this page.
      </Typography>
      <Button
        variant="contained"
        onClick={() => navigate("/")}
        sx={{
          mt: 3,
          backgroundColor: "#1976d2",
          color: "white",
          borderRadius: "20px",
          px: 4,
          py: 1,
          boxShadow: "0 4px 12px rgba(255, 64, 129, 0.4)",
          "&:hover": { backgroundColor: "#115293" },
        }}
      >
        Go to Home
        
      </Button>
    </Box>
  );
};

export default Forbidden;
