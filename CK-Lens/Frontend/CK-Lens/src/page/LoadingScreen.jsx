import React from "react";
import { Box, Typography } from "@mui/material";

const LoadingScreen = ({ message = "Loading..." }) => {
  return (
    <Box
      sx={{
        height: "100vh",
        width: "100vw",
        backdropFilter: "blur(10px)",
        backgroundColor: "rgba(0, 0, 0, 0.1)",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: 3,
        zIndex: 9999,
        position: "fixed",
        top: 0,
        left: 0,
      }}
    >
      {/* Animated bars */}
      <Box sx={{ display: "flex", gap: 1 }}>
        {[0, 1, 2, 3, 4].map((i) => (
          <Box
            key={i}
            sx={{
              width: 8,
              height: 20,
              borderRadius: 1,
              backgroundColor: i % 2 === 0 ? "#ea2b45" : "#1976d2",
              animation: `bounceBar 1.2s ${i * 0.1}s infinite ease-in-out`,
              transformOrigin: "bottom center",
            }}
          />
        ))}
      </Box>

      {/* Animation styles */}
      <style>{`
        @keyframes bounceBar {
          0%, 100% {
            transform: scaleY(1);
            opacity: 0.4;
          }
          50% {
            transform: scaleY(3);
            opacity: 1;
          }
        }
      `}</style>

      {/* Text */}
      <Typography
        variant="h5"
        fontWeight="bold"
        color="#1976d2"
      >
        {message}
      </Typography>
    </Box>
  );
};

export default LoadingScreen;
