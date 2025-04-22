import api from "../api/axios";

export const registerAWSAccount = async ({ arn, accountName, accountNumber, accountRegion }) => {
    const payload = {
        arn,
        accountName,
        accountNumber: Number(accountNumber),
        accountRegion,
    };

    const response = await api.post("/onboarding/aws-account", payload);
    return response.data;
};
