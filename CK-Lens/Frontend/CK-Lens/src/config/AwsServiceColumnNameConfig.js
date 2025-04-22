const config = {
    EC2: {
        columns: [
            { label: "Resource ID", key: "id" },
            { label: "Resource Name", key: "name" },
            { label: "Region", key: "region" },
            { label: "Status", key: "status" },
        ],
    },
    RDS: {
        columns: [
            { label: "Resource ID", key: "id" },
            { label: "Resource Name", key: "name" },
            { label: "Engine", key: "engine" },
            { label: "Region", key: "region" },
            { label: "Status", key: "status" },
        ],
    },
    ASG: {
        columns: [
            { label: "Resource ID", key: "id" },
            { label: "Resource Name", key: "name" },
            { label: "Region", key: "region" },
            { label: "Desired Size", key: "desiredCapacity" },
            { label: "Min Size", key: "minSize" },
            { label: "Max Size", key: "maxSize" },
            { label: "Status", key: "status" },
        ],
    },
};

export default config;
