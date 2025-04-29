import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useLocation, Navigate, useNavigate, Outlet } from "react-router-dom";
import { Box, Container } from "@mui/material";
import Footer from "../components/footer/Footer";
import Header from "../components/header/Header";
import { logout } from "../redux/actions/authActions";
import { persistor } from "../redux/store";
import Cookies from "js-cookie";

function Root() {
  const location = useLocation();
  const dispatch = useDispatch();
  const reduxToken = useSelector((state) => state.auth.token);
  const accessToken = Cookies.get("token");
  const token = accessToken || reduxToken;
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
  ) : (
    <Navigate
      to="/login"
      state={{ from: location }}
      replace
    />
  );
}

export default Root;
