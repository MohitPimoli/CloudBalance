import api from "../api/axios";

export const fetchAccounts = async (userid) => {
    if (userid) {
        const response = await api.get("/user/all-accounts", { params: { userId: userid } });
        return response.data;
    } else {
        const response = await api.get("/user/account/all");
        return response.data;
    }
};