import React from "react";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";

const RoleBasedRedirect = () => {
  const role = useSelector((state) => state.auth.user?.role);
  if (!role)
    return (
      <Navigate
        to="/login"
        replace
      />
    );

  switch (role?.toUpperCase()) {
    case "ADMIN":
    case "READ-ONLY":
      return (
        <Navigate
          to="/user-management"
          replace
        />
      );
    case "CUSTOMER":
      return (
        <Navigate
          to="/cost-explorer"
          replace
        />
      );
    default:
      return (
        <Navigate
          to="/unauthorized"
          replace
        />
      );
  }
};

export default RoleBasedRedirect;
