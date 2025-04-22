import React from "react";
import { useSelector } from "react-redux";
import { Navigate, useLocation } from "react-router-dom";

const ProtectedRoute = ({
  Component,
  requiredDashboard,
  requiredPermission,
}) => {
  const location = useLocation();
  const { token, dashboardPermissions } = useSelector((state) => state.auth);
  const isAuthenticated = !!token;

  const hasPermission = dashboardPermissions.some(
    (perm) =>
      perm.dashboard === requiredDashboard &&
      (requiredPermission === "READ"
        ? ["READ", "EDIT"].includes(perm.permissionType)
        : perm.permissionType === "EDIT")
  );

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        state={{ from: location }}
        replace
      />
    );
  }

  if (!hasPermission) {
    return (
      <Navigate
        to="/unauthorized"
        replace
      />
    );
  }

  return <Component />;
};

export default ProtectedRoute;
