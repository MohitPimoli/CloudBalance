import api from "../api/axios";

export const fetchEC2Instances = async (accountNumber) => {
    const res = await api.get("/aws/ec2", { params: { accountNumber } });
    return res.data;
};

export const fetchRDSInstances = async (accountNumber) => {
    const res = await api.get("/aws/rds", { params: { accountNumber } });
    return res.data;
};

export const fetchASGInstances = async (accountNumber) => {
    const res = await api.get("/aws/asg", { params: { accountNumber } });
    return res.data;
};

export const fetchAccounts = async () => {
    try {
        const response = await api.get("/aws/account-by-role");
        return response?.data;
    } catch (err) {
        console.error("Error fetching accounts", err);
        return [];
    }
};