import React from "react";
import {
  Box,
  Typography,
  TextField,
  Stack,
  IconButton,
  Tooltip,
  InputAdornment,
  FormControlLabel,
  Checkbox,
  RadioGroup,
  Radio,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";

import curReportDetail from "../../assets/curReportDetail.png";
import configS3Bucket from "../../assets/configS3Bucket.png";
import reportDeliveryOptions from "../../assets/reportDeliveryOptions.png";

import onboardingCURConfig from "../../config/OnboardingStepsConfig";

const imageMap = {
  "CUR-report-details": curReportDetail,
  "Set-S3-Bucket-Image-reference": configS3Bucket,
  "Report-Delivery-Options-Image-Reference": reportDeliveryOptions,
};

const Step3CreateCUR = () => {
  const step = onboardingCURConfig.step3;

  const handleCopyText = (value) => {
    navigator.clipboard.writeText(value);
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
          backgroundColor: "white",
        }}
      >
        {step.instructions.map((step, index) => {
          switch (step.type) {
            case "text":
              return (
                <Typography key={index}>
                  <strong>{textStep++}.</strong> {step.content}
                </Typography>
              );
            case "input":
              return (
                <Box
                  mt={1}
                  key={index}
                >
                  <TextField
                    value={step.value}
                    variant="outlined"
                    size="small"
                    InputProps={{
                      readOnly: true,
                      endAdornment: (
                        <InputAdornment position="end">
                          <Tooltip title="Copy to clipboard">
                            <IconButton
                              onClick={() => handleCopyText(step.value)}
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
            case "checkbox":
              return (
                <Box
                  key={index}
                  sx={{ display: "flex", alignItems: "center", gap: 1 }}
                >
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked
                        disabled
                      />
                    }
                    label={step.label}
                  />
                </Box>
              );
            case "radio":
              return (
                <Box key={index}>
                  <Typography variant="body1">{step.groupLabel}</Typography>
                  <RadioGroup value={step.value}>
                    {step.options.map((option, idx) => (
                      <FormControlLabel
                        key={idx}
                        value={option.toLowerCase()}
                        control={<Radio />}
                        label={option}
                      />
                    ))}
                  </RadioGroup>
                </Box>
              );
            case "image":
              return (
                <Box key={index}>
                  <img
                    src={imageMap[step.alt] || ""}
                    alt={step.alt}
                    style={{ maxWidth: "100%", borderRadius: 8 }}
                  />
                </Box>
              );
            default:
              return null;
          }
        })}
      </Stack>
    </Box>
  );
};

export default Step3CreateCUR;
