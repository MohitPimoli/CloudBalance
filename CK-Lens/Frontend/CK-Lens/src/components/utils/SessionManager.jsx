import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { logout } from "../../redux/actions/authActions";

const SessionManager = ({ children }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  useEffect(() => {
    const token = sessionStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        const currentTime = Math.floor(Date.now() / 1000);
        const timeLeft = decoded.exp - currentTime;
        if (timeLeft <= 0) {
          dispatch(logout());
          sessionStorage.clear();
          navigate("/login");
        } else {
          const timeout = setTimeout(() => {
            dispatch(logout());
            sessionStorage.clear();
            navigate("/login");
          }, timeLeft * 1000);
          return () => clearTimeout(timeout);
        }
      } catch (err) {
        console.error("JWT decode error:", err);
        dispatch(logout());
        sessionStorage.clear();
        navigate("/login");
      }
    }
  }, [dispatch, navigate]);
  return <>{children}</>;
};

export default SessionManager;
