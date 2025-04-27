import React, { useState } from "react";
import MonthPicker from "./MonthPicker";
import ChartWrapper from "./ChartWrapper";
import dayjs from "dayjs";
import {
  Box,
  ToggleButtonGroup,
  ToggleButton,
  Paper,
  Typography,
  CircularProgress,
  Card,
  useTheme,
} from "@mui/material";
import DynamicFilterFields from "./DynamicFilterFields";
import { ChartColumn, ChartLine, AlertTriangle } from "lucide-react";
import { fetchCostData } from "../../services/costExplorerApis";
import { useQuery } from "@tanstack/react-query";
import CostDataTable from "./CostDataTable";

const CostExplorerDataWrapper = ({
  selectedAccount,
  selectedOption,
  toggleFilter,
  filters,
}) => {
  const theme = useTheme();
  const [chartType, setChartType] = useState("line");
  const [startDate, setStartDate] = useState(dayjs().startOf("month"));
  const [endDate, setEndDate] = useState(dayjs().endOf("month"));
  const [appliedFilters, setAppliedFilters] = useState({});

  const groupTotals = {};
  const payload = {
    accountId: selectedAccount,
    groupBy: selectedOption,
    startDate: startDate.format("YYYY-MM-DD"),
    endDate: endDate.format("YYYY-MM-DD"),
    filters: Object.entries(appliedFilters).map(
      ([columnName, filterValues]) => ({
        columnName,
        filterValues,
      })
    ),
  };

  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ["cost-data", payload],
    queryFn: () => fetchCostData(payload),
    enabled: !!selectedAccount && !!selectedOption,
  });

  const handleApplyFilters = (filtersObj) => {
    setAppliedFilters(filtersObj);
    refetch();
  };

  if (isLoading) {
    return (
      <Paper
        elevation={2}
        sx={{
          p: 4,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
          borderRadius: 2,
          minHeight: "400px",
        }}
      >
        <CircularProgress size={40} />
        <Typography
          variant="body1"
          sx={{ mt: 2, color: "text.secondary" }}
        >
          Loading cost data...
        </Typography>
      </Paper>
    );
  }

  if (isError) {
    return (
      <Paper
        elevation={2}
        sx={{
          p: 4,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
          borderRadius: 2,
          backgroundColor: theme.palette.error.light,
          minHeight: "200px",
        }}
      >
        <AlertTriangle
          size={40}
          color={theme.palette.error.main}
        />
        <Typography
          variant="h6"
          sx={{ mt: 2, color: theme.palette.error.main }}
        >
          Error Loading Data
        </Typography>
        <Typography
          variant="body2"
          sx={{ mt: 1, color: theme.palette.error.dark }}
        >
          There was a problem fetching cost data. Please try again later.
        </Typography>
      </Paper>
    );
  }

  const uniqueDates = [...new Set(data?.data.map((item) => item.date))].sort();
  const categories = [
    {
      category: uniqueDates.map((date) => ({ label: date })),
    },
  ];

  data?.data.forEach((item) => {
    const name = item.groupBy || "Total";
    if (!groupTotals[name]) {
      groupTotals[name] = 0;
    }
    groupTotals[name] += Math.abs(item.cost);
  });

  const sortedGroupNames = Object.keys(groupTotals).sort(
    (a, b) => groupTotals[b] - groupTotals[a]
  );

  const topGroups = sortedGroupNames.slice(0, 5);

  const dataset = [...topGroups, "Others"].map((groupType) => {
    const values = uniqueDates.map((date) => {
      if (groupType === "Others") {
        const sumOthers = data?.data
          .filter(
            (item) =>
              item.date === date && !topGroups.includes(item.groupBy || "Total")
          )
          .reduce((sum, item) => sum + Math.abs(item.cost), 0);
        return { value: sumOthers.toFixed(2) };
      } else {
        const match = data?.data.find(
          (item) =>
            item.date === date && (item.groupBy || "Total") === groupType
        );
        return { value: match ? Math.abs(match.cost).toFixed(2) : "0" };
      }
    });

    return {
      seriesname: groupType,
      data: values,
    };
  });

  // Check if we have any data to display
  const hasData = dataset.some((series) =>
    series.data.some((item) => parseFloat(item.value) > 0)
  );

  const chartConfigs = {
    type: chartType === "line" ? "msline" : "mscolumn2d",
    width: "100%",
    height: "400",
    dataFormat: "json",
    dataSource: {
      chart: {
        caption: `Cost Explorer - ${selectedOption}`,
        subcaption: `${startDate.format("MMM DD, YYYY")} to ${endDate.format(
          "MMM DD, YYYY"
        )}`,
        xAxisName: "Date",
        yAxisName: "Cost (USD)",
        theme: "fusion",
        drawCrossLine: "1",
        showLegend: "1",
        legendPosition: "bottom",
        legendNumRows: "2",
        legendBorderAlpha: "0",
        legendShadow: "0",
        plotHighlightEffect: "fadeout",
        // usePlotGradientColor: "1",
        // plotGradientColor: "#ffffff",
        usePlotGradientColor: "0",
        showPlotBorder: "0",
        showHoverEffect: "1",
        showValues: "0",
        showToolTip: "1",
        toolTipBorderColor: "#666666",
        toolTipBgColor: "#ffffff",
        toolTipBgAlpha: "85",
        toolTipBorderThickness: "1",
        toolTipBorderAlpha: "100",
        toolTipPadding: "6",
        alignCaptionWithCanvas: "0",
        captionPadding: "15",
        dataEmptyMessage: "No cost data available for the selected criteria.",
        dataEmptyMessageColor: "#666666",
        dataEmptyMessageFontSize: "14",
      },
      categories,
      dataset,
    },
  };

  return (
    <Paper
      elevation={3}
      sx={{
        borderRadius: 2,
        overflow: "hidden",
      }}
    >
      {/* Header with title and controls */}
      <Box
        sx={{
          p: 2,
          borderBottom: `1px solid ${theme.palette.divider}`,
          bgcolor: theme.palette.background.paper,
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          alignItems: { xs: "stretch", md: "center" },
          justifyContent: "space-between",
          gap: 2,
        }}
      >
        <Typography
          variant="h6"
          fontWeight={500}
          color="text.primary"
        >
          {`Cost Analysis by ${selectedOption}`}
        </Typography>

        <Box
          sx={{
            display: "flex",
            flexDirection: { xs: "column", sm: "row" },
            gap: 2,
            alignItems: { xs: "stretch", sm: "center" },
            justifyContent: "flex-end",
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
            size="small"
            aria-label="chart type"
            sx={{
              height: "fit-content",
              "& .MuiToggleButton-root": {
                border: `1px solid ${theme.palette.divider}`,
              },
            }}
          >
            <ToggleButton
              value="line"
              aria-label="line chart"
            >
              <ChartLine size={18} />
            </ToggleButton>
            <ToggleButton
              value="bar"
              aria-label="bar chart"
            >
              <ChartColumn size={18} />
            </ToggleButton>
          </ToggleButtonGroup>
        </Box>
      </Box>

      {/* Main content area with chart and filters */}
      <Box
        sx={{
          display: "flex",
          flexDirection: { xs: "column", lg: "row" },
          width: "100%",
          p: 2,
          gap: 2,
          bgcolor: theme.palette.grey[50],
        }}
      >
        {/* Chart area */}
        <Box
          sx={{
            flex: { xs: "1 1 100%", lg: 3 },
            minWidth: 0,
            display: "flex",
            flexDirection: "column",
          }}
        >
          <Card
            elevation={1}
            sx={{
              borderRadius: 2,
              overflow: "hidden",
              height: "100%",
              display: "flex",
              flexDirection: "column",
            }}
          >
            {!hasData ? (
              <Box
                sx={{
                  p: 4,
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  justifyContent: "center",
                  height: "400px",
                  textAlign: "center",
                }}
              >
                <Typography
                  variant="h6"
                  color="text.secondary"
                  gutterBottom
                >
                  No Cost Data Available
                </Typography>
                <Typography
                  variant="body2"
                  color="text.secondary"
                >
                  There is no cost data for the selected criteria. Try changing
                  your filters or date range.
                </Typography>
              </Box>
            ) : (
              <ChartWrapper chartConfigs={chartConfigs} />
            )}
          </Card>
        </Box>

        {/* Filters panel (conditionally rendered) */}
        {toggleFilter && (
          <Box
            sx={{
              flex: { xs: "1 1 100%", lg: 1 },
              minWidth: { xs: "100%", lg: 250 },
              maxWidth: { lg: 320 },
            }}
          >
            <DynamicFilterFields
              fields={filters}
              onApplyFilters={handleApplyFilters}
            />
          </Box>
        )}
      </Box>

      {/* Table area */}
      <Box sx={{ p: 2, pt: 0 }}>
        <Typography
          variant="subtitle1"
          sx={{ mt: 2, mb: 1, fontWeight: 500 }}
        >
          Detailed Cost Data
        </Typography>
        <CostDataTable data={data?.data} />
      </Box>
    </Paper>
  );
};

export default CostExplorerDataWrapper;
