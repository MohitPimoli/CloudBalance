import api from "../api/axios";

export const fetchDistinctValues = async (fieldName) => {
    const response = await api.get("/cost/filter", {
        params: { fieldName: fieldName },
    });
    return response.data;
};


export const fetchDisplayNames = async () => {
    const response = await api.get("/cost/display-names");
    return response.data;
};

export const fetchData = async (payload) => {
    const response = await api.post("/cost/data", payload);
    return response.data;
};

const fetchCostData = async (payload) => {
    const response = await axios.post("/cost/data", payload);
    return response.data.data;
};
