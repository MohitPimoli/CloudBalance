import Cookies from "js-cookie";
const initialState = {
  user: null,
  dashboardPermissions: [],
  token: null,
  switchUser: null,
  switchUserToken: null,
  switchUserPermissions: [],
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'LOGIN_SUCCESS':

      return {
        ...state,
        token: action.payload.token,
        user: {
          id: action.payload.id,
          username: action.payload.username,
          email: action.payload.email,
          role: action.payload.role,
        },
        dashboardPermissions: action.payload.dashboardPermissions,
      };
    case 'SWITCH_USER_SUCCESS':
      return {
        ...state,
        switchUser: {
          id: action.payload.id,
          username: action.payload.username,
          role: action.payload.role,

        },
        switchUserToken: action.payload.token,
        switchUserPermissions: action.payload.dashboardPermissions,
      };
    case 'LOGOUT':
      Cookies.remove("token");
      return initialState;
    default:
      return state;
  }
};
export default authReducer;