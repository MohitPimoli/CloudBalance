const initialState = {
  user: null,
  dashboardPermissions: [],
  token: null,
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
    case 'LOGOUT':
      sessionStorage.removeItem("token");
      return initialState;
    default:
      return state;
  }
};
export default authReducer;
