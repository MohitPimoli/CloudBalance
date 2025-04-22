// src/store/sidebar/sidebarActions.js
export const TOGGLE_SIDEBAR = "sidebar/toggleSidebar";
export const SET_SIDEBAR_OPEN = "sidebar/setSidebarOpen";

export const toggleSidebar = () => ({
    type: TOGGLE_SIDEBAR,
});

export const setSidebarOpen = (isOpen) => ({
    type: SET_SIDEBAR_OPEN,
    payload: isOpen,
});
