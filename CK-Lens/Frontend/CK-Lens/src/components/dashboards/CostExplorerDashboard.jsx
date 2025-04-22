import React from "react";
import { Box, Typography, Button, Stack } from "@mui/material";
import { House } from "lucide-react";

const CostExplorerDashboard = () => {
  return (
    <Box sx={{ mt: 8, ml: 10, pr: 8 }}>
      {/* Breadcrumb */}
      <Stack
        direction="row"
        alignItems="center"
        spacing={1}
        mb={2}
      >
        <House
          color="#1976d2"
          onClick={() => navigate("/")}
          style={{ cursor: "pointer" }}
        />
        <Typography
          color="#1976d2"
          variant="body2"
        >
          {">"}
        </Typography>
        <Typography
          color="#1976d2"
          onClick={() => navigate("/aws-services")}
          style={{ cursor: "pointer" }}
        >
          Cost Explorer
        </Typography>
      </Stack>
      <Typography variant="h4">Cost Explorer</Typography>
      <Typography
        variant="body"
        color="#1976d2"
      >
        How to always aware of cost charges and history.
      </Typography>
      <Box
        sx={{
          width: "100%",
          borderBottom: "1px solid lightgray",
          mt: 1,
        }}
      />
      <Box>{/* child component land here */}</Box>
    </Box>
  );
};

export default CostExplorerDashboard;
