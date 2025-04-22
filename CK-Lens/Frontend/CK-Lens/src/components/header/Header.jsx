import React from "react";
import {
  AppBar,
  Toolbar,
  Button,
  Box,
  IconButton,
  CssBaseline,
  Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import MenuIcon from "@mui/icons-material/Menu";
import logo from "../../assets/logo.png";
import { logout } from "../../redux/actions/authActions";
import Sidebar from "../sidebar/Sidebar";
import { toggleSidebar } from "../../redux/actions/sidebarAction";
import { persistor } from "../../redux/store";
import { UsersRound, LogOut } from "lucide-react";
import { logoutUser } from "../../services/authServiceApis";

const Header = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const user = useSelector((state) => state.auth.user);
  const open = useSelector((state) => state.sidebar.open);

  const handleToggle = () => {
    dispatch(toggleSidebar());
  };

  const handleLogout = async () => {
    try {
      const res = await logoutUser();
      if (res.status === 200) {
        dispatch(logout());
        persistor.purge();
        navigate("/login");
      }
    } catch (error) {
      if (error?.response?.status === 401) {
        dispatch(logout());
        navigate("/login");
      } else {
        console.error("Logout error:", error);
      }
    }
  };

  return (
    <Box sx={{ display: "flex" }}>
      <CssBaseline />
      <AppBar
        position="fixed"
        color="white"
        sx={{
          backgroundColor: "#ffffff",
          boxShadow: "none",
          padding: "0 10px",
          borderBottom: "1px solid #e0e0e0",
          zIndex: (theme) => theme.zIndex.drawer + 1,
          ml: open ? "240px" : "60px",
          transition: "margin-left 0.3s ease-in-out",
        }}
      >
        <Toolbar>
          <Box
            display="flex"
            sx={{ flexGrow: 1, width: "100%" }}
          >
            <img
              src={logo}
              alt="CloudBalance Logo"
              style={{ height: 60, cursor: "pointer" }}
              onClick={() => navigate("/")}
            />
            <IconButton
              color="inherit"
              aria-label="open drawer"
              edge="start"
              onClick={handleToggle}
              sx={{ ml: 0.5 }}
            >
              <MenuIcon />
            </IconButton>
          </Box>

          <Box
            display="flex"
            alignItems="center"
            sx={{ marginLeft: "auto" }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                gap: 1.5,
                p: 1,
              }}
            >
              <IconButton sx={{ p: 1, border: "1px solid #4398d7" }}>
                <UsersRound color="#4398d7" />
              </IconButton>

              <Box>
                <Typography
                  variant="subtitle2"
                  sx={{ fontWeight: "bold", color: "text.secondary" }}
                >
                  Welcome!
                </Typography>
                <Typography
                  variant="body1"
                  sx={{ color: "#4398d7" }}
                >
                  {user.username}
                </Typography>
              </Box>
            </Box>
            <Box
              sx={{ borderRight: "1px solid lightgray", height: "40px", mr: 2 }}
            />
            <Button
              variant="outlined"
              color="primary"
              sx={{
                fontSize: "16px",
                padding: "5px 20px",
                textTransform: "none",
                backgroundColor: "#ffffff",
                "&:hover": {
                  color: "red",
                  borderColor: "red",
                  backgroundColor: "#ffb19b",
                },
              }}
              onClick={handleLogout}
            >
              <LogOut style={{ marginRight: "4px" }} />
              LOGOUT
            </Button>
          </Box>
        </Toolbar>
      </AppBar>
      <Sidebar
        open={open}
        handleDrawerToggle={handleToggle}
      />
    </Box>
  );
};

export default Header;
