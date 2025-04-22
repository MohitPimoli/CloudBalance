const getStatusColor = (status) => {
    const normalized = status?.toLowerCase();

    switch (normalized) {
        case "active":
        case "running":
        case "available":
            return { label: normalized.toUpperCase(), color: "#22c55e" };
        case "disabled":
        case "inactive":
        case "stopped":
        case "stopping":
            return { label: normalized.toUpperCase(), color: "#ef4444" };
        default:
            return { label: normalized?.toUpperCase() || "UNKNOWN", color: "#facc15" };
    }
};
export default getStatusColor;