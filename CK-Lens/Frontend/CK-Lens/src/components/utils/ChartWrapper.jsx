import React, { useState } from "react";
import FusionCharts from "fusioncharts";
import Charts from "fusioncharts/fusioncharts.charts";
import FusionTheme from "fusioncharts/themes/fusioncharts.theme.fusion";
import ReactFC from "react-fusioncharts";
import { Box } from "@mui/material";

ReactFC.fcRoot(FusionCharts, Charts, FusionTheme);

const ChartWrapper = ({ chartConfigs }) => {
  console.log("Chart-Data:", chartConfigs);
  return (
    <Box>
      <ReactFC {...chartConfigs} />
    </Box>
  );
};
export default ChartWrapper;
