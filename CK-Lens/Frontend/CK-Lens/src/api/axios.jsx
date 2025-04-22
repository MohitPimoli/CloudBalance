import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

const getToken = () => {
  try {
    return sessionStorage.getItem("token") || null;
  } catch (error) {
    console.error("Error getting token from sessionStorage:", error);
    return null;
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
  (error) => {
    const { response } = error;
    console.error("Response error:", response);
    if (!response) {
      console.error("Network/Server error:", error);
      window.location.href = "/server-error";
      return Promise.reject(error);
    }

    console.error(
      `HTTP ${response.status}:`,
      response.data?.message || error.message
    );

    switch (response.status) {
      case 401:
        window.location.href = "/login";
        break;
      case 403:
        window.location.href = "/forbidden";
        break;
      case 404:
        window.location.href = "/not-found";
        break;
      case 500:
      case 502:
      case 503:
      case 504:
        window.location.href = "/server-error";
        break;
      default:
        break;
    }

    return Promise.reject(response);
    filteredUsers;
  }
);

export default api;
