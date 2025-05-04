import React from "react";
import { Box, Typography } from "@mui/material";
import { motion } from "framer-motion";
import config from "../config/ThankyouPageConfig";

const ThankYouPage = () => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      minHeight="80vh"
      textAlign="center"
    >
      {/* Your SVG Icon */}
      <Box
        component={motion.div}
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Box
          component="img"
          src={config.svg}
          alt="Success checkmark"
          sx={{ width: 100, height: 100, mb: 2 }}
        />
      </Box>

      {/* Thank You Text */}
      <Typography
        variant="h6"
        fontWeight="bold"
      >
        {config.t1}
        <Box
          component="span"
          sx={{ color: "#4398D7" }}
        >
          Cl
        </Box>
        <Box
          component="span"
          sx={{ color: "#D64794" }}
        >
          o
        </Box>
        <Box
          component="span"
          sx={{ color: "#4398D7" }}
        >
          ud
        </Box>
        <Box
          component="span"
          sx={{ color: "#253E66" }}
        >
          Balance
        </Box>{" "}
        Lens!
      </Typography>

      <Typography
        variant="body1"
        align="center"
        sx={{ mt: 2, maxWidth: 600, mx: "auto", color: "text.secondary" }}
      >
        {config.p}
      </Typography>
    </Box>
  );
};

export default ThankYouPage;
