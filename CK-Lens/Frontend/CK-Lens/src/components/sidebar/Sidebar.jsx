import React from "react";
import {
  Drawer,
  List,
  ListItemButton,
  ListItemText,
  ListItemIcon,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import config from "../../config/SidebarTabsConfig";

const drawerWidth = 240;

const Sidebar = ({ open }) => {
  const navigate = useNavigate();
  const dashboardPermissions = useSelector(
    (state) => state.auth.dashboardPermissions
  );

  const handleNavigation = (path) => {
    if (window.location.pathname !== path) {
      navigate(path);
    }
  };

  const accessibleTabs = config.tabs.filter((tab) =>
    dashboardPermissions.some(
      (permission) =>
        permission.dashboard === tab.key &&
        (permission.permissionType === "EDIT" ||
          permission.permissionType === "READ")
    )
  );

  return (
    <Drawer
      variant="persistent"
      anchor="left"
      open={open}
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: drawerWidth,
          marginTop: "64px",
        },
      }}
    >
      <List>
        {accessibleTabs.map((item) => {
          const IconComponent = item.icon;
          return (
            <ListItemButton
              sx={{
                borderRadius: "8px",
                py: 1.2,
                px: 2,
                bgcolor:
                  window.location.pathname === item.path
                    ? "#e3f2fd"
                    : "transparent",
                "&:hover": {
                  backgroundColor: "#f1f1f1",
                },
              }}
              key={item.text}
              onClick={() => handleNavigation(item.path)}
            >
              {IconComponent && (
                <ListItemIcon
                  sx={{
                    color:
                      window.location.pathname === item.path
                        ? "#1976d2"
                        : "#666",
                  }}
                >
                  <IconComponent />
                </ListItemIcon>
              )}
              <ListItemText
                primary={item.text}
                primaryTypographyProps={{
                  fontSize: "15px",
                  fontWeight: 500,
                  color:
                    window.location.pathname === item.path ? "#1976d2" : "#333",
                }}
              />
            </ListItemButton>
          );
        })}
      </List>
    </Drawer>
  );
};

export default Sidebar;
