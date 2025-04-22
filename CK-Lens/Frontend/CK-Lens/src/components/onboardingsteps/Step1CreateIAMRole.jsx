import React from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Stack,
  IconButton,
  Tooltip,
  InputAdornment,
  MenuItem,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import config from "../../config/OnboardingStepsConfig.js";
import iamRole from "../../assets/IAM-Role.png";

const Step1CreateIAMRole = ({
  formValues,
  setFormValues,
  touched,
  setTouched,
  conflictField,
}) => {
  const step = config.step1;
  const handleCopy = async (text) => {
    try {
      await navigator.clipboard.writeText(text);
    } catch (err) {
      console.error("Failed to copy: ", err);
    }
  };
  let textStep = 1;
  return (
    <Box p={4}>
      <Typography
        variant="h5"
        fontWeight="bold"
        gutterBottom
      >
        {step.title}
      </Typography>
      <Typography mb={3}>{step.subtitle}</Typography>

      <Stack
        spacing={3}
        sx={{
          borderRadius: 2,
          border: "2px solid #e0e0e0",
          p: 2,
          bgcolor: "white",
        }}
      >
        {step.instructions.map((item, index) => {
          if (item.type === "text") {
            return (
              <Typography key={index}>
                <strong>{textStep++}.</strong> {item.content}
              </Typography>
            );
          }

          if (item.type === "code") {
            return (
              <Paper
                key={index}
                variant="outlined"
                sx={{
                  position: "relative",
                  p: 2,
                  bgcolor: "#f9f9f9",
                  maxHeight: 300,
                  overflow: "auto",
                }}
              >
                <Tooltip title="Copy to clipboard">
                  <IconButton
                    size="small"
                    onClick={() => handleCopy(item.code)}
                    sx={{ position: "sticky", top: 0, left: "100%", zIndex: 1 }}
                  >
                    <ContentCopyIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
                <pre style={{ margin: 0, color: "#337ab7" }}>{item.code}</pre>
              </Paper>
            );
          }

          if (item.type === "input") {
            return (
              <Box
                key={index}
                mt={1}
              >
                <TextField
                  value={item.value}
                  variant="outlined"
                  size="small"
                  InputProps={{
                    readOnly: true,
                    endAdornment: (
                      <InputAdornment position="end">
                        <Tooltip title="Copy to clipboard">
                          <IconButton
                            onClick={() => handleCopy(item.value)}
                            size="small"
                          >
                            <ContentCopyIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </InputAdornment>
                    ),
                  }}
                />
              </Box>
            );
          }

          if (item.type === "image") {
            return (
              <Box key={index}>
                <img
                  src={iamRole}
                  alt={item.alt || "IAM Role reference"}
                />
              </Box>
            );
          }

          return null;
        })}

        <Box
          sx={{
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-between",
            gap: 2,
            mt: 2,
            mb: 2,
          }}
        >
          {Object.entries(step.fields).map(([key, field]) => {
            const value = formValues[key];
            const isTouched = touched[key];
            const isValid = field.regex ? field.regex.test(value) : !!value;
            const showError = isTouched && field.required && !isValid;
            const isConflict = conflictField === key;
            return (
              <TextField
                key={key}
                required={field.required}
                fullWidth
                select={field.type === "select"}
                label={field.label}
                placeholder={field.placeholder}
                value={value}
                onChange={(e) =>
                  setFormValues((prev) => ({ ...prev, [key]: e.target.value }))
                }
                onBlur={() => setTouched((prev) => ({ ...prev, [key]: true }))}
                error={showError || isConflict}
                helperText={
                  isConflict
                    ? "This value already exists. Please enter a unique one."
                    : showError
                    ? value
                      ? field.message
                      : field.prompt
                    : " "
                }
                size="medium"
                variant="outlined"
                SelectProps={{
                  MenuProps: {
                    PaperProps: {
                      style: {
                        maxHeight: 48 * 5 + 8,
                      },
                    },
                  },
                }}
              >
                {field.type === "select" &&
                  field.options?.map((option) => (
                    <MenuItem
                      key={option.value}
                      value={option.value}
                    >
                      {option.label}
                    </MenuItem>
                  ))}
              </TextField>
            );
          })}
        </Box>
      </Stack>
    </Box>
  );
};

export default Step1CreateIAMRole;
