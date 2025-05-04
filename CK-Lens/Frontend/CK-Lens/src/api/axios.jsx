import axios from "axios";
import Cookies from "js-cookie";
import { navigate } from "../services/navigationService";
import { persistor } from "../redux/store";

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

const getToken = () => {
  try {
    return Cookies.get("token") || null;
  } catch (error) {
    console.error("Error getting token from cookieStorage:", error);
    return null;
  }
};

const setTokens = (accessToken) => {
  try {
    Cookies.set("token", accessToken, { secure: true, sameSite: "Strict" });
  } catch (error) {
    console.error("Error saving tokens:", error);
  }
};

api.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const { response, config } = error;

    if (!response) {
      console.error("Network/Server error:", error);
      navigate("/server-error");
      return Promise.reject(error);
    }

    const originalRequest = config;

    if (
      response.status === 401 &&
      response.data?.errorCode === 1001 &&
      !originalRequest._retry
    ) {
      if (isRefreshing) {
        try {
          const newToken = await new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          });
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return api(originalRequest);
        } catch (err) {
          console.error("Failed to process queue after refresh:", err);
          return Promise.reject(err);
        }
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshResponse = await axios.post(
          `${import.meta.env.VITE_API_BASE_URL}/auth/refresh`,
          {},
          { withCredentials: true }
        );

        const { accessToken } = refreshResponse.data;

        setTokens(accessToken);

        api.defaults.headers.Authorization = `Bearer ${accessToken}`;
        processQueue(null, accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        persistor.purge();
        navigate("/login");
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    if (response.status === 401) {
      navigate("/login");
    } else if (response.status === 403) {
      navigate("/forbidden");
    } else if (response.status === 404) {
      navigate("/not-found");
    } else if ([500, 502, 503, 504].includes(response.status)) {
      navigate("/server-error");
    }

    return Promise.reject(error);
  }
);

export default api;
