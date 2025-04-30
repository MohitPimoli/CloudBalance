import React from "react";
import { Snackbar, Alert } from "@mui/material";

const SnackBar = ({ snackbar, setSnackbar }) => {
  return (
    <Snackbar
      open={snackbar.open}
      autoHideDuration={6000}
      onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
    >
      <Alert
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        severity={snackbar.severity}
        sx={{ width: "100%" }}
      >
        {snackbar.message}
      </Alert>
    </Snackbar>
  );
};
export default SnackBar;
