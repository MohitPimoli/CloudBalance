import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#4398d7",
    },
    secondary: {
      main: "#d64794",
    },
    info: {
      main: "#2196f3",
    },
    background: {
      default: "#f9f9f9",
    },
  },
  typography: {
    fontFamily: "Roboto, Arial, sans-serif",
    h6: {
      fontWeight: 600,
    },
    button: {
      textTransform: "none",
    },
  },
});

export default theme;
