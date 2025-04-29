let navigateFunction = null;

export const setNavigate = (navigate) => {
    navigateFunction = navigate;
};

export const navigate = (to, options) => {
    if (navigateFunction) {
        navigateFunction(to, options);
    } else {
        console.error("Navigate function is not set yet!");
    }
};
