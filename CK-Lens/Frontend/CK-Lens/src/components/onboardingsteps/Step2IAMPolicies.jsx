import React, { useState } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Stack,
  IconButton,
  Tooltip,
  InputAdornment,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import ckTunerRole from "../../assets/ckTunerRole.png";
import permissionPolicy from "../../assets/permissionPolicy.png";
import otherPermissionPolicy from "../../assets/otherPermissionPolicy.png";
import createInlinePolicy from "../../assets/createInlinePolicy.png";
import onboardingFormConfig from "../../config/OnboardingStepsConfig";

const imageMap = {
  "CK-Tuner Role Screenshot": ckTunerRole,
  "Attack Policy Screenshot": permissionPolicy,
  "Select Customer Policies Screenshot": otherPermissionPolicy,
  "Create Inline Policy Screenshot": createInlinePolicy,
};

const Step2IAMPolicies = () => {
  const [copyTooltip, setCopyTooltip] = useState("Copy to clipboard");
  const step = onboardingFormConfig;

  const handleCopy = (value) => {
    navigator.clipboard.writeText(value).then(() => {
      setCopyTooltip("Copied!");
      setTimeout(() => setCopyTooltip("Copy to clipboard"), 1000);
    });
  };
  let textStep = 1;
  const renderBlock = (block, idx) => {
    switch (block.type) {
      case "text":
        return (
          <Typography key={idx}>
            <strong>{textStep++}.</strong> {block.content}
          </Typography>
        );

      case "code":
        return (
          <Paper
            key={idx}
            variant="outlined"
            sx={{
              position: "relative",
              p: 2,
              bgcolor: "#f9f9f9",
              maxHeight: 300,
              overflow: "auto",
            }}
          >
            <Tooltip title={copyTooltip}>
              <IconButton
                size="small"
                onClick={() => handleCopy(block.code)}
                sx={{ position: "sticky", top: 0, left: "100%", zIndex: 1 }}
              >
                <ContentCopyIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            <pre style={{ margin: 0, color: "#337ab7" }}>{block.code}</pre>
          </Paper>
        );

      case "input":
        return (
          <Box
            mt={1}
            key={idx}
          >
            <TextField
              value={block.value}
              variant="outlined"
              size="small"
              InputProps={{
                readOnly: true,
                endAdornment: (
                  <InputAdornment position="end">
                    <Tooltip title={copyTooltip}>
                      <IconButton
                        onClick={() => handleCopy(block.value)}
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

      case "image":
        return (
          <Box key={idx}>
            <img
              src={imageMap[block.alt]}
              alt={block.alt}
              style={{ maxWidth: "100%" }}
            />
          </Box>
        );

      default:
        return null;
    }
  };

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
          backgroundColor: "white",
        }}
      >
        {step.instructions.map((block, idx) => renderBlock(block, idx))}
      </Stack>
    </Box>
  );
};

export default Step2IAMPolicies;
