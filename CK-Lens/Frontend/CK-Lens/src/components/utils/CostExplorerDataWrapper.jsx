import React, { useState } from "react";
import MonthPicker from "./MonthPicker";
import ChartWrapper from "./ChartWrapper";
import dayjs from "dayjs";
import { Box, ToggleButtonGroup, ToggleButton } from "@mui/material";
import TableWrapper from "./TableWrapper";
import DynamicFilterFields from "./DynamicFilterFields";
import { ChartColumn, ChartLine } from "lucide-react";

const CostExplorerDataWrapper = ({ selectedOption, toggleFilter, filters }) => {
  const [chartType, setChartType] = useState("line");
  const [startDate, setStartDate] = useState(dayjs().startOf("month"));
  const [endDate, setEndDate] = useState(dayjs().endOf("month"));

  const chartConfigs = {
    type: chartType,
    width: "100%",
    height: "400",
    dataFormat: "json",
    dataSource: {
      chart: {
        caption: "Cost ($) by Service",
        xAxisName: "Service",
        yAxisName: "Cost in $",
        theme: "fusion",
      },
      // data: chartData,
    },
  };

  console.log("StartDate:", startDate);
  console.log("StartDate:", endDate);
  return (
    <>
      <Box>
        <Box
          sx={{
            maxWidth: "auto",
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-between",
            mb: 2,
          }}
        >
          <MonthPicker
            startDate={startDate}
            setStartDate={setStartDate}
            endDate={endDate}
            setEndDate={setEndDate}
          />

          <ToggleButtonGroup
            value={chartType}
            exclusive
            onChange={(e, val) => val && setChartType(val)}
          >
            <ToggleButton value="line">
              {" "}
              <ChartLine />
            </ToggleButton>
            <ToggleButton value="bar">
              <ChartColumn />
            </ToggleButton>
          </ToggleButtonGroup>
        </Box>
      </Box>
      <Box
        sx={{
          display: "flex",
          flexDirection: "row",
          width: "100%",
          overflow: "hidden",
        }}
      >
        <Box sx={{ flex: 3, minWidth: 0 }}>
          <ChartWrapper chartConfigs={chartConfigs} />
        </Box>

        {toggleFilter && (
          <Box sx={{ flex: 1, minWidth: 250, overflowX: "auto" }}>
            <DynamicFilterFields
              fields={filters}
              onApplyFilters={(filters) => {
                console.log("Selected filters:", filters);
              }}
            />
          </Box>
        )}
      </Box>

      <TableWrapper></TableWrapper>
    </>
  );
};
export default CostExplorerDataWrapper;
