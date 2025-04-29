import React, { useMemo } from "react";
import DynamicReusableTable from "./DynamicReusableTable"; // Adjust path if needed
import { Typography } from "@mui/material";

const CostDataTable = ({ data = [], appliedGroupBy }) => {
  const processed = useMemo(() => {
    const monthSet = new Set();
    const groupedData = {};

    data.forEach(({ groupBy, cost, date }) => {
      monthSet.add(date);
      if (!groupedData[groupBy]) {
        groupedData[groupBy] = {};
      }
      groupedData[groupBy][date] = (groupedData[groupBy][date] || 0) + cost;
    });

    const months = Array.from(monthSet).sort();
    const rows = [];
    let subtotalPerMonth = {};
    let grandTotal = 0;

    Object.entries(groupedData).forEach(([groupName, monthCosts]) => {
      let rowTotal = 0;
      const row = {
        groupBy: groupName,
      };

      months.forEach((month) => {
        const cost = monthCosts[month] || 0;
        row[month] = cost;
        rowTotal += cost;

        subtotalPerMonth[month] = (subtotalPerMonth[month] || 0) + cost;
      });

      row.total = rowTotal;
      grandTotal += rowTotal;
      rows.push(row);
    });

    const footerRow = {
      groupBy: "Total",
    };

    months.forEach((month) => {
      footerRow[month] = Number((subtotalPerMonth[month] || 0).toFixed(2));

    });

    footerRow.total = Number(grandTotal.toFixed(2));

    const columns = [
      { label: appliedGroupBy, key: "groupBy" },
      ...months.map((month) => ({ label: month, key: month })),
      { label: "Total", key: "total" },
    ];

    return { columns, rows, footerRow };
  }, [data, appliedGroupBy]);

  return (
    <DynamicReusableTable
      columns={processed.columns}
      data={processed.rows}
      footerData={processed.footerRow}
      getRowId={(row) => row.groupBy}
      enableFilters={false}
      headBgColor="#1e3a8a"
      headTextColor="white"
      renderCell={(row, key) => {
        const value = row[key];
        if (typeof value === "number") {
          return <Typography variant="body2">${value.toFixed(2)}</Typography>;
        }
        return value;
      }}
    />
  );
};

export default CostDataTable;
