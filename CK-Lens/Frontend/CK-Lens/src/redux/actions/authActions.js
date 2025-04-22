export const loginSuccess = (userData) => {
    sessionStorage.setItem("token", userData.token);
    return {
        type: 'LOGIN_SUCCESS',
        payload: {
            token: userData.token,
            id: userData.id,
            username: userData.username,
            email: userData.email,
            role: userData.role,
            dashboardPermissions: userData.dashboardPermissions,
        },
    };
};

export const logout = () => {
    return {
        type: 'LOGOUT',
    };
};
