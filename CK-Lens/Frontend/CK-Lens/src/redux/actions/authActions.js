import Cookies from "js-cookie";

export const loginSuccess = (userData) => {
    Cookies.set("token", userData.token, { secure: true, sameSite: "Strict" });

    return {
        type: 'LOGIN_SUCCESS',
        payload: {
            token: userData.token,
            id: userData.id,
            username: userData.username,
            role: userData.role,
            dashboardPermissions: userData.dashboardPermissions,
        },
    };
};

export const switchUserSuccess = (switchUserData,token) => ({
    type: 'SWITCH_USER_SUCCESS',
    payload: {
        id: switchUserData.id,
        username: switchUserData.username,
        role: switchUserData.role,
        dashboardPermissions: switchUserData.dashboardPermissions,
        token: token,
    },
});

export const logout = () => {
    return {
        type: 'LOGOUT',
    };
};

