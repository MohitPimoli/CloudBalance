import PeopleIcon from "@mui/icons-material/People";
import RocketLaunchIcon from "@mui/icons-material/RocketLaunch";
import ShowChartIcon from "@mui/icons-material/ShowChart";
import CloudIcon from "@mui/icons-material/Cloud";

const config = {
    tabs: [
        {
            text: "Users",
            path: "/",
            key: "USER_MANAGEMENT",
            icon: PeopleIcon,
        },
        {
            text: "Onboarding",
            path: "/onboarding",
            key: "ONBOARDING",
            icon: RocketLaunchIcon,
        },
        {
            text: "Cost Explorer",
            path: "/cost-explorer",
            key: "COST_EXPLORER",
            icon: ShowChartIcon,
        },
        {
            text: "AWS Services",
            path: "/aws-services",
            key: "AWS_SERVICES",
            icon: CloudIcon,
        },
    ],
};

export default config;
