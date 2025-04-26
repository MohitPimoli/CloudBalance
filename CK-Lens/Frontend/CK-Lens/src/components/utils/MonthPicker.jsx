import React from "react";
import { Box, TextField } from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

const MonthPicker = ({ startDate, setStartDate, endDate, setEndDate }) => {
const smallTextFieldStyles = {
  "& .MuiInputBase-root": {
    height: 36,
    fontSize: 14,
    padding: "0 8px",
  },
  "& .MuiInputBase-input": {
    padding: "8px 0",
    fontSize: 14,
  },
  "& .MuiInputLabel-root": {
    fontSize: 13,
  },
  "& .MuiSvgIcon-root": {
    fontSize: 18,
  },
};

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box
        display="flex"
        gap={2}
        alignItems="center"
      >
        <DatePicker
          label="Start Date"
          value={startDate}
          onChange={(newValue) => setStartDate(newValue)}
          renderInput={(params) => (
            <TextField
              {...params}
              sx={smallTextFieldStyles}
            />
          )}
        />
        <DatePicker
          label="End Date"
          value={endDate}
          onChange={(newValue) => setEndDate(newValue)}
          renderInput={(params) => (
            <TextField
              {...params}
              sx={smallTextFieldStyles}
            />
          )}
          minDate={startDate}
        />
      </Box>
    </LocalizationProvider>
  );
};

export default MonthPicker;
