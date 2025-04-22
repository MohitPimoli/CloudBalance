import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useLocation, Navigate, useNavigate, Outlet } from "react-router-dom";
import { Box, Container } from "@mui/material";
import Footer from "../components/footer/Footer";
import Header from "../components/header/Header";
import SessionManager from "../components/utils/SessionManager";
import { logout } from "../redux/actions/authActions";
import { persistor } from "../redux/store";

function Root() {
  const location = useLocation();
  const dispatch = useDispatch();
  const reduxToken = useSelector((state) => state.auth.token);
  const sessionToken = sessionStorage.getItem("token");
  const token = sessionToken || reduxToken;
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      dispatch(logout());
      persistor.purge();
      navigate("/login");
    }
  }, [token, dispatch]);

  const isAuthenticated = !!token;

  return isAuthenticated ? (
    <SessionManager>
      <Box
        display="flex"
        flexDirection="column"
        minHeight="100vh"
      >
        <Header />
        <Container
          disableGutters
          maxWidth={false}
          sx={{
            pl: 30,
            mt: 4,
          }}
        >
          <Outlet />
        </Container>
        <Footer />
      </Box>
    </SessionManager>
  ) : (
    <Navigate
      to="/login"
      state={{ from: location }}
      replace
    />
  );
}

export default Root;
