import api from "../api/axios";
import JSEncrypt from 'jsencrypt';

export const logoutUser = async () => {
    const response = await api.get("/auth/logout");
    return response;
};

export const loginUser = async (payload) => {
    const response = await api.post("/auth/login", payload);
    return response;
};

export const encryptPass = async (rawPassword) => {
    try {
        const publicKey = await api.get("/auth/public-key");
        const encrypt = new JSEncrypt();
        encrypt.setPublicKey(publicKey.data);
        const encryptedPass = encrypt.encrypt(rawPassword);
        if (!encryptedPass) throw new Error("Encryption failed");
        return encryptedPass;
    } catch (error) {
        console.error("Encryption error:", error);
        throw error;
    }
};

