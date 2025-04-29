import React, { useEffect } from "react";
import {
  createBrowserRouter,
  RouterProvider,
  useNavigate,
} from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Root from "./layout/Root.jsx";
import LoginPage from "./page/login.jsx";
import ProtectedRoute from "./routes/ProtectedRoutes.jsx";
import NotFound from "./page/NotFound.jsx";
import Forbidden from "./page/Forbidden.jsx";
import Unauthorized from "./page/Unauthorized.jsx";
import InternalServerError from "./page/ServerError.jsx";
import UserManagementDashboard from "./page/UserManagement.jsx";
import AddNewUser from "./components/utils/AddNewUser.jsx";
import RoleBasedRedirect from "./routes/RoleBasedRedirect.jsx";
import LoadingScreen from "./page/LoadingScreen.jsx";
import AWSServiceWrapper from "./components/dashboards/AWSServicesWrapper.jsx";
import OnboardingWrapper from "./components/dashboards/OnboardingWrapper.jsx";
import CostExplorerDashboard from "./components/dashboards/CostExplorerDashboard.jsx";
import { setNavigate } from "./services/navigationService.js";

const queryClient = new QueryClient();

const InitNavigateWrapper = ({ children }) => {
  const navigate = useNavigate();

  useEffect(() => {
    setNavigate(navigate);
  }, [navigate]);

  return children;
};
const router = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/",
    element: (
      <InitNavigateWrapper>
        <Root />
      </InitNavigateWrapper>
    ),
    children: [
      {
        index: true,
        element: <RoleBasedRedirect />,
      },
      {
        path: "/user-management",
        element: (
          <ProtectedRoute
            requiredDashboard="USER_MANAGEMENT"
            requiredPermission="READ"
            Component={UserManagementDashboard}
          />
        ),
      },
      {
        path: "/add-new-user",
        element: (
          <ProtectedRoute
            requiredDashboard="USER_MANAGEMENT"
            requiredPermission="EDIT"
            Component={AddNewUser}
          />
        ),
      },
      {
        path: "/user-management/modify/:userId",
        element: (
          <ProtectedRoute
            requiredDashboard="USER_MANAGEMENT"
            requiredPermission="EDIT"
            Component={AddNewUser}
          />
        ),
      },
      {
        path: "/onboarding",
        element: (
          <ProtectedRoute
            requiredDashboard="ONBOARDING"
            requiredPermission="EDIT"
            Component={OnboardingWrapper}
          />
        ),
      },
      {
        path: "/cost-explorer",
        element: (
          <ProtectedRoute
            requiredDashboard="COST_EXPLORER"
            requiredPermission="READ"
            Component={CostExplorerDashboard}
          />
        ),
      },
      {
        path: "/loading",
        element: (
          <ProtectedRoute
            requiredDashboard="USER_MANAGEMENT"
            requiredPermission="READ"
            Component={LoadingScreen}
          />
        ),
      },
      {
        path: "/aws-services",
        element: (
          <ProtectedRoute
            requiredDashboard="AWS_SERVICES"
            requiredPermission="READ"
            Component={AWSServiceWrapper}
          />
        ),
      },
    ],
  },
  {
    path: "/forbidden",
    element: <Forbidden />,
  },
  {
    path: "/unauthorized",
    element: <Unauthorized />,
  },
  {
    path: "/server-error",
    element: <InternalServerError />,
  },

  {
    path: "*",
    element: <NotFound />,
  },
]);

const App = () => (
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
  </QueryClientProvider>
);

export default App;
