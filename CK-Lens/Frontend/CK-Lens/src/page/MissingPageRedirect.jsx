import React from "react";
import { Box, Typography, CircularProgress } from "@mui/material";

const MissingPageRedirect = () => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      justifyContent="center"
      alignItems="center"
      minHeight="100vh"
      bgcolor="#f5f5f5"
      textAlign="center"
      px={2}
    >
      <Typography
        variant="h4"
        fontWeight={600}
        color="error"
        gutterBottom
      >
        Oops!
      </Typography>
      <Typography
        variant="h6"
        color="textSecondary"
        gutterBottom
      >
        The page you were looking for is missing.
      </Typography>
      <Typography
        variant="body1"
        color="textSecondary"
        mb={4}
      >
        Redirecting...
      </Typography>
      <CircularProgress color="error" />
    </Box>
  );
};

export default MissingPageRedirect;
