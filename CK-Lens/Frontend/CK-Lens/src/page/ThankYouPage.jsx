import React from "react";
import { Box, Typography } from "@mui/material";
import ThankYouSVG from "../assets/green_tick_check.svg";
import { motion } from "framer-motion";

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
          src={ThankYouSVG}
          alt="Success checkmark"
          sx={{ width: 100, height: 100, mb: 2 }}
        />
      </Box>

      {/* Thank You Text */}
      <Typography
        variant="h6"
        fontWeight="bold"
      >
        Thank You For Onboarding with{" "}
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
        We appreciate you completing the setup process with our tool{" "}
        <strong>CloudBalance Lens</strong>. You're all set to start using it.
      </Typography>
    </Box>
  );
};

export default ThankYouPage;
