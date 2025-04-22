import api from "../api/axios";


export const fetchUsers = async (page) => {
    const response = await api.get("/user/all", {
        params: { page, size: 15 },
    });
    const data = response.data;
    return {
        users: data?.content ?? [],
        hasNextPage: !data?.last,
        page: data?.number ?? 0,
        totalPages: data?.totalPages ?? 0,
    };
};

export const getUserById = async (userId) => {
    const response = await api.get("/user/get", { params: { userId: userId } });
    return response;
};

export const registerUser = async (payload) => {
    const response = await api.post("/user/register", payload);
    return response.data;
};

export const updateUser = async (payload) => {
    const response = await api.put("/user/update", payload);
    return response.data;
};