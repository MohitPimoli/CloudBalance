import React from "react";
import {
  Popover,
  Typography,
  Box,
  Button,
  CircularProgress,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { useDispatch, useSelector } from "react-redux";
import {
  getAllCustomer,
  getUserPermissions,
} from "../../services/userManagementServiceApis";
import { SetSwitchUser } from "../../redux/actions/switchUserAction";
import { switchUserSuccess } from "../../redux/actions/authActions";
import { useQuery } from "@tanstack/react-query";

const SwitchUserButton = ({ anchorEl, onClose }) => {
  const open = Boolean(anchorEl);
  const dispatch = useDispatch();

  const { user, switchUser, token } = useSelector((state) => state.auth);
  const isSwitchedByAdmin = useSelector((state) => state.switch.isSwitched);

  const { data: users = [], isLoading } = useQuery({
    queryKey: ["allCustomers", open],
    queryFn: async () => {
      const res = await getAllCustomer();
      const allUsers = res.data;
      return [
        ...allUsers.filter((u) => u.id === selectedUserId),
        ...allUsers.filter((u) => u.id !== selectedUserId),
      ];
    },
    enabled: open,
  });

  const handleSwitch = async (userId) => {
    if (isSwitchedByAdmin) {
      dispatch(SetSwitchUser());
      onClose();
      return;
    }

    try {
      const res = await getUserPermissions(userId);
      dispatch(switchUserSuccess(res.data, token));
      dispatch(SetSwitchUser());
      onClose();
    } catch (err) {
      console.error("Error switching user:", err);
    }
  };

  const selectedUserId = isSwitchedByAdmin ? switchUser?.id : user?.id;

  return (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
      sx={{
        "& .MuiPopover-paper": {
          border: "1px solid #115293",
          borderRadius: "8px",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.2)",
        },
      }}
    >
      <Box sx={{ p: 1, minWidth: 275, maxHeight: "400px", overflowY: "auto" }}>
        <Box
          sx={{
            position: "sticky",
            top: 0,
            zIndex: 1,
            border: "1px solid #ff4081",
            borderRadius: "4px",
            background: "#90caf9",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            mb: 1,
          }}
        >
          <Typography
            variant="h6"
            gutterBottom
          >
            Switch User
          </Typography>
        </Box>
        {isLoading ? (
          <CircularProgress size={24} />
        ) : (
          <List>
            {users.map((u) => {
              const isCurrent = u.id === selectedUserId;
              return (
                <ListItem
                  key={u.id}
                  secondaryAction={
                    <Button
                      variant="outlined"
                      size="small"
                      color={
                        isCurrent && isSwitchedByAdmin ? "error" : "#90caf9"
                      }
                      onClick={() => handleSwitch(u.id)}
                      disabled={
                        (isSwitchedByAdmin && !isCurrent) ||
                        (!isSwitchedByAdmin && isCurrent)
                      }
                    >
                      {isCurrent
                        ? isSwitchedByAdmin
                          ? "Back"
                          : "Current"
                        : "Switch"}
                    </Button>
                  }
                >
                  <ListItemText primary={`${u.firstName} ${u.lastName}`} />
                </ListItem>
              );
            })}
          </List>
        )}
      </Box>
    </Popover>
  );
};

export default SwitchUserButton;
