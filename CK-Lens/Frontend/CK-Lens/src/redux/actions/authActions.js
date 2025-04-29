import Cookies from "js-cookie";

export const loginSuccess = (userData) => {
    Cookies.set("token", userData.token, { secure: true, sameSite: "Strict" });

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
