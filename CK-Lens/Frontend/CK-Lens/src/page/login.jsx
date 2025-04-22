import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  TextField,
  Button,
  Paper,
  Link,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import FormConfig from "../config/FormConfig";
import logo from "../assets/logo.png";
import { useDispatch } from "react-redux";
import { loginSuccess } from "../redux/actions/authActions";
import { loginUser, encryptPass } from "../services/authServiceApis";

const LoginPage = () => {
  const loginForm = FormConfig(false).filter(
    (field) => field.name === "username" || field.name === "password"
  );

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [formValues, setFormValues] = useState({
    username: "",
    password: "",
  });

  const [formErrors, setFormErrors] = useState({
    username: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [responseMessage, setResponseMessage] = useState("");
  const [error, setError] = useState("");

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const validateForm = () => {
    let valid = true;
    const errors = {};

    loginForm.forEach((field) => {
      const value = formValues[field.name];
      const rules = field.rules;

      if (rules.required && !value.trim()) {
        errors[field.name] = `${field.label} is required`;
        valid = false;
      } else if (rules.minLength && value.length < rules.minLength) {
        errors[
          field.name
        ] = `${field.label} must be at least ${rules.minLength} characters`;
        valid = false;
      } else if (rules.maxLength && value.length > rules.maxLength) {
        errors[
          field.name
        ] = `${field.label} must be less than ${rules.maxLength} characters`;
        valid = false;
      } else {
        errors[field.name] = "";
      }
    });

    setFormErrors(errors);
    return valid;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues((prev) => ({ ...prev, [name]: value }));
    setFormErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setResponseMessage("");
    setError("");

    const isValid = validateForm();
    if (!isValid) return;

    setLoading(true);

    try {
      const encryptedPassword = await encryptPass(formValues.password);
      console.log("password", encryptedPassword);
      const payload = {
        username: formValues.username,
        password: encryptedPassword,
      };
      const response = await loginUser(payload);
      const data = response.data;
      dispatch(loginSuccess(data));
      setResponseMessage(data.message || "Login successful");
      navigate("/");
    } catch (err) {
      if (err?.status === 429) {
        setError("Too many requests. Please try again later.");
      } else if (err?.status === 408) {
        setError("Request timed out. Please try again.");
      } else if (err?.data?.message) {
        setError(err.data.message);
      } else {
        setError("An unexpected error occurred. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        backgroundColor: "#f5f5f5",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Paper
        elevation={3}
        sx={{
          px: 5,
          py: 10,
          width: {
            xs: "90%",
            sm: 400,
            md: 450,
            lg: 500,
          },
          height: "auto",
          borderRadius: 3,
          backgroundColor: "white",
        }}
      >
        <Box
          display="flex"
          justifyContent="center"
          mb={3}
        >
          <img
            src={logo}
            alt="CloudBalance Logo"
            style={{ height: 100 }}
          />
        </Box>

        <form onSubmit={handleSubmit}>
          {loginForm.map((field) => (
            <TextField
              key={field.name}
              fullWidth
              label={field.label}
              name={field.name}
              type={
                field.name === "password" && showPassword ? "text" : field.type
              }
              placeholder={field.placeholder}
              value={formValues[field.name]}
              onChange={handleChange}
              required={field.required}
              margin="normal"
              error={!!formErrors[field.name]}
              helperText={formErrors[field.name]}
              InputProps={
                field.name === "password"
                  ? {
                      endAdornment: (
                        <InputAdornment position="end">
                          <IconButton
                            onClick={togglePasswordVisibility}
                            edge="end"
                          >
                            {showPassword ? <VisibilityOff /> : <Visibility />}
                          </IconButton>
                        </InputAdornment>
                      ),
                    }
                  : undefined
              }
            />
          ))}

          <Button
            fullWidth
            variant="contained"
            color="primary"
            type="submit"
            disabled={loading}
            sx={{ mt: 2 }}
          >
            {loading ? (
              <CircularProgress
                size={24}
                color="inherit"
              />
            ) : (
              "Login"
            )}
          </Button>
        </form>

        <Box
          display="flex"
          justifyContent="flex-end"
          mt={2}
        >
          <Link
            href="#"
            variant="body2"
          >
            Forgot Password?
          </Link>
        </Box>

        {error && (
          <Alert
            severity="error"
            sx={{ mt: 2 }}
          >
            {error}
          </Alert>
        )}
        {responseMessage && (
          <Alert
            severity="success"
            sx={{ mt: 2 }}
          >
            {responseMessage}
          </Alert>
        )}
      </Paper>
    </Box>
  );
};

export default LoginPage;
