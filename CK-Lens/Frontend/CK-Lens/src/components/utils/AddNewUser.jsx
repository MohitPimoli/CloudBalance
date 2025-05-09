import React, { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  CircularProgress,
  Alert,
  Box,
  TextField,
  Breadcrumbs,
  Link,
  Button,
  Grid,
  MenuItem,
  Typography,
  Paper,
} from "@mui/material";
import { useForm, Controller } from "react-hook-form";
import AccountIdAssociation from "../utils/AccountSelectBox";
import FormConfig from "../../config/FormConfig";
import {
  getUserById,
  registerUser,
  updateUser,
} from "../../services/userManagementServiceApis";
import { encryptPass } from "../../services/authServiceApis";
import SnackBar from "./SnackBar";

const AddNewUser = () => {
  const { userId } = useParams();
  const isEditMode = !!userId;
  const typo = isEditMode ? "Update User" : "Add New User";
  const navigate = useNavigate();
  const addUserFormConfig = FormConfig(isEditMode);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "error",
  });

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      firstName: "",
      lastName: "",
      username: "",
      email: "",
      password: "",
      role: "",
    },
  });

  const [selectedRole, setSelectedRole] = useState("");
  const [loading, setLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");
  const [linkedAccounts, setLinkedAccounts] = useState([]);

  const fetchUserData = useCallback(() => {
    if (!isEditMode || !userId) return;
    getUserById(userId)
      .then((res) => {
        const userData = res.data;
        if (userData) {
          reset({
            firstName: userData.firstName || "",
            lastName: userData.lastName || "",
            email: userData.email || "",
            password: "",
            role: userData.roleName || "",
          });
          setSelectedRole(userData.roleName || "");
        }
      })
      .catch((err) => {
        console.error("Failed to fetch user", err);
      });
  }, [userId, isEditMode, reset]);

  useEffect(() => {
    if (isEditMode) {
      fetchUserData();
    }
  }, [isEditMode, fetchUserData]);

  const handleLinkedAccountsChange = useCallback((accounts, role) => {
    if (role === "CUSTOMER") {
      setLinkedAccounts(accounts.map((acc) => acc.accountId));
    }
  }, []);

  const onSubmit = async (data) => {
    setLoading(true);
    try {
      let encryptedPassword = "";
      if (data.password) {
        encryptedPassword = await encryptPass(data.password);
      }

      const payload = {
        userDTO: {
          id: isEditMode ? userId : null,
          firstName: data.firstName,
          lastName: data.lastName,
          username: data.username,
          email: data.email,
          password: encryptedPassword,
          roleName: data.role,
        },
        accountIds: selectedRole === "CUSTOMER" ? linkedAccounts : [],
      };

      if (isEditMode) {
        const response = await updateUser(payload);
        setSnackbar({
          open: true,
          message: response?.message || "User updated successfully!",
          severity: "success",
        });
      } else {
        const response = await registerUser(payload);
        setSnackbar({
          open: true,
          message: response?.message || "User registered successfully!",
          severity: "success",
        });
        setSuccessMsg("User registered successfully!");
      }
      reset();
      setSelectedRole("");
      setLinkedAccounts([]);
      setErrorMsg("");
      setTimeout(() => {
        navigate("/");
      }, 3000);
    } catch (error) {
      setErrorMsg(error?.response?.data?.message || "Operation failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 1000, mx: "auto", mt: 5 }}>
      {/* Breadcrumb */}
      <Breadcrumbs
        aria-label="breadcrumb"
        sx={{ mb: 2 }}
      >
        <SnackBar
          snackbar={snackbar}
          setSnackbar={setSnackbar}
        />
        <Link
          underline="hover"
          color="inherit"
          onClick={() => {
            reset();
            setSelectedRole("");
            setLinkedAccounts([]);
            setErrorMsg("");
            navigate("/");
          }}
          sx={{ cursor: "pointer" }}
        >
          User
        </Link>
        <Typography color="text.primary">{typo}</Typography>
      </Breadcrumbs>
      <Typography
        variant="h5"
        mb={1}
      >
        {typo}
      </Typography>
      {successMsg && (
        <Alert
          severity="success"
          sx={{ mb: 2 }}
        >
          {successMsg}
        </Alert>
      )}
      {errorMsg && (
        <Alert
          severity="error"
          sx={{ mb: 2 }}
        >
          {errorMsg}
        </Alert>
      )}
      <Box sx={{ borderBottom: "1px solid lightgray", width: "100%", mb: 2 }} />
      <Paper
        elevation={3}
        sx={{ p: 3 }}
      >
        <form onSubmit={handleSubmit(onSubmit)}>
          <Grid
            container
            spacing={3}
          >
            {addUserFormConfig.map((field) => (
              <Grid
                key={field.name}
                size={{ xs: 12, sm: 6 }}
              >
                <Controller
                  name={field.name}
                  control={control}
                  rules={field.rules}
                  render={({ field: controllerField }) => {
                    if (
                      field.type === "select" &&
                      Array.isArray(field.options)
                    ) {
                      return (
                        <>
                          <TextField
                            sx={{ mr: 10 }}
                            select
                            fullWidth
                            label={field.label}
                            value={controllerField.value || ""}
                            onChange={(e) => {
                              controllerField.onChange(e.target.value);
                              setSelectedRole(e.target.value);
                            }}
                            error={!!errors[field.name]}
                            helperText={errors[field.name]?.message}
                          >
                            {field.options.map((option) => (
                              <MenuItem
                                key={option}
                                value={option}
                              >
                                {option}
                              </MenuItem>
                            ))}
                          </TextField>
                        </>
                      );
                    }
                    return (
                      <TextField
                        fullWidth
                        label={field.label}
                        type={field.type}
                        {...controllerField}
                        error={!!errors[field.name]}
                        helperText={errors[field.name]?.message}
                      />
                    );
                  }}
                />
              </Grid>
            ))}
          </Grid>
          <Box/>
          {selectedRole === "CUSTOMER" && (
            <AccountIdAssociation
              userid={isEditMode ? userId : null}
              role={selectedRole}
              onLinkedAccountsChange={handleLinkedAccountsChange}
            />
          )}
          <Grid
            textAlign="right"
            size={{ xs: 12}}
          >
            <Button
              variant="contained"
              color="primary"
              type="submit"
              sx={{ mt: 3 }}
              disabled={loading}
              startIcon={loading && <CircularProgress size={18} />}
            >
              {loading ? "Submitting..." : "Submit"}
            </Button>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
};

export default AddNewUser;
