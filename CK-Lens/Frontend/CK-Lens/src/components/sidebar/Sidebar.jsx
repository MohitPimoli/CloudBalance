import React from "react";
import {
  Drawer,
  List,
  ListItemButton,
  ListItemText,
  ListItemIcon,
  Tooltip,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import config from "../../config/SidebarTabsConfig";

const expandedWidth = 240;
const collapsedWidth = 60;

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
      open={true}
      sx={{
        width: open ? expandedWidth : collapsedWidth,
        flexShrink: 0,
        whiteSpace: "nowrap",
        transition: "width 0.3s",
        "& .MuiDrawer-paper": {
          width: open ? expandedWidth : collapsedWidth,
          marginTop: "64px",
          overflowX: "hidden",
          transition: "width 0.3s",
          boxShadow: "2px 0 5px rgba(0, 0, 0, 0.3)",
        },
      }}
    >
      <List>
        {accessibleTabs.map((item) => {
          const IconComponent = item.icon;
          const isActive = window.location.pathname === item.path;

          return (
            <Tooltip
              title={!open ? item.text : ""}
              placement="right"
              key={item.text}
            >
              <ListItemButton
                sx={{
                  borderRadius: "8px",
                  py: 1.2,
                  px: open ? 2 : 1.5,
                  bgcolor: isActive ? "#e3f2fd" : "transparent",
                  "&:hover": {
                    backgroundColor: "#f1f1f1",
                    "& .MuiListItemIcon-root": {
                      color: "#1976d2",
                    },
                    "& .MuiListItemText-primary": {
                      color: "#1976d2",
                    },
                  },
                  justifyContent: open ? "flex-start" : "center",
                }}
                onClick={() => handleNavigation(item.path)}
              >
                {IconComponent && (
                  <ListItemIcon
                    sx={{
                      minWidth: 0,
                      mr: open ? 2 : 0,
                      color: isActive ? "#1976d2" : "#666",
                      justifyContent: "center",
                    }}
                  >
                    <IconComponent
                      sx={{
                        fontSize: 30,
                      }}
                    />
                  </ListItemIcon>
                )}
                {open && (
                  <ListItemText
                    primary={item.text}
                    primaryTypographyProps={{
                      fontSize: "15px",
                      fontWeight: 500,
                      color: isActive ? "#1976d2" : "#333",
                    }}
                  />
                )}
              </ListItemButton>
            </Tooltip>
          );
        })}
      </List>
    </Drawer>
  );
};

export default Sidebar;
