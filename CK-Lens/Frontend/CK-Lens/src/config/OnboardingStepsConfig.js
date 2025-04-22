import config from "./OnboardingCodeBoxConfig";

const onboardingFormConfig = {
    step1: {
        title: "Create an IAM Role",
        subtitle: "Create an IAM Role by following these steps",
        instructions: [
            {
                type: "text",
                content: 'Log into AWS account & Create an IAM Role.',
            },
            {
                type: "text",
                content: 'In the Trusted entity type section, select Custom trust policy. Replace the prefilled policy with the policy below:',
            },
            {
                type: "code",
                code: JSON.stringify(config.policy.trustPolicy, null, 2),
            },
            {
                type: "text",
                content: 'Click on Next to go to the Add permissions page. We won\'t add any permissions now.',
            },
            {
                type: "text",
                content: 'In the Role name field, enter the role name below and click Create Role -',
            },
            {
                type: "input",
                value: config.policyName.trustPolicyName,
            },
            {
                type: "text",
                content: "Go to the newly created IAM Role and copy the Role ARN -",
            },
            {
                type: "image",
                alt: "CK-Tuner IAM Role Screenshot",
            },
            {
                type: "text",
                content: "Paste the copied Role ARN, Account Number and Account Name below -",
            },
        ],
        fields: config.fields
    },
    step2: {
        title: "Add Customer Managed Policies",
        subtitle: "Create an Inline policy for the role by following these steps",
        instructions: [
            {
                type: "text",
                content: 'Go to the "Create Policy" Page.',
            },
            {
                type: "text",
                content: 'Click on the JSON tab and paste the following policy and click on Next:',
            },
            {
                type: "code",
                code: JSON.stringify(config.policy.costAuditPolicy, null, 2),
            },
            {
                type: "text",
                content: "In the Name field, enter below-mentioned policy name and click on Create Policy",
            },
            {
                type: "input",
                value: config.policyName.costAuditPolicy,
            },
            {
                type: "text",
                content: "Again, go to the Create Policy Page.",
            },
            {
                type: "text",
                content: "Click on the JSON tab and paste the following policy and click on Next:",
            },
            {
                type: "code",
                code: JSON.stringify(config.policy.secAuditPolicy, null, 2),
            },
            {
                type: "text",
                content: "In the Name field, enter below-mentioned policy name and click on Create Policy",
            },
            {
                type: "input",
                value: config.policyName.secAuditPolicy,
            },
            {
                type: "text",
                content: "Again, go to the Create Policy Page.",
            },
            {
                type: "text",
                content: "Click on the JSON tab and paste the following policy and click on Next:",
            },
            {
                type: "code",
                code: JSON.stringify(config.policy.costAuditPolicy, null, 2),
            },
            {
                type: "text",
                content: "In the Name field, enter below-mentioned policy name and click on Create Policy",
            },
            {
                type: "input",
                value: config.policyName.costExplorerPolicy,
            },
            {
                type: "text",
                content: "Go to the CK-Tuner-Role",
            },
            {
                type: "image",
                alt: "CK-Tuner Role Screenshot",
            },
            {
                type: "text",
                content: "In Permission policies, click on Add permissions > Attach Policy",
            },
            {
                type: "image",
                alt: "Attach Policy Screenshot",
            },
            {
                type: "text",
                content:
                    "Filter by Type > Customer managed then search for cktuner-CostAuditPolicy, cktuner-SecAuditPolicy, cktuner-TunerReadEssentials and select them.",
            },
            {
                type: "image",
                alt: "Select Customer Policies Screenshot",
            },
            {
                type: "text",
                content: "Now, click on Add permissions",
            },
            {
                type: "text",
                content: "In Permission policies, click on Add permissions > Create inline policy",
            },
            {
                type: "image",
                alt: "Create Inline Policy Screenshot",
            },
            {
                type: "text",
                content: "Click on the JSON tab and paste the following policy",
            },
            {
                type: "code",
                code: JSON.stringify(config.policy.permissionInlinePolicy, null, 2),
            },
            {
                type: "text",
                content: "Now, click on Review policy.",
            },
            {
                type: "text",
                content:
                    "In the Name field, enter the below-mentioned policy name and click on Create Policy",
            },
            {
                type: "input",
                value: config.policyName.permissionInlinePolicy,
            },
        ],
    },
    step3: {
        title: "Create Cost & Usage Report",
        subtitle: "Create a Cost & Usage Report by following these steps",
        instructions: [
            {
                type: "text",
                content:
                    'Go to Cost and Usage Reports in the Billing Dashboard and click on Create report.',
            },
            {
                type: "text",
                content:
                    "Name the report as shown below and select the Include resource IDs checkbox:",
            },
            {
                type: "input",
                value: config.policyName.cur,
            },
            {
                type: "checkbox",
                label: "Include Resource IDs",
            },
            {
                type: "text",
                content: "Click on Next",
            },
            {
                type: "image",
                alt: "CUR-report-details",
            },
            {
                type: "text",
                content:
                    "In Configure S3 Bucket, provide the name of the S3 bucket that was created:",
            },
            {
                type: "checkbox",
                label: "The following default policy will be applied to your bucket.",
            },
            {
                type: "text",
                content: "Click on Save",
            },
            {
                type: "image",
                alt: "Set-S3-Bucket-Image-reference",
            },
            {
                type: "text",
                content:
                    "In the Delivery options section, enter the below-mentioned Report path prefix:",
            },
            {
                type: "input",
                value: config.policyName.reportPathPrefix,
            },
            {
                type: "text",
                content: "Additionally, ensure that the following checks are in place:",
            },
            {
                type: "radio",
                groupLabel: "Time granularity",
                value: "hourly",
                options: ["Hourly"],
            },
            {
                type: "checkbox",
                label: "Amazon Athena",
            },
            {
                type: "image",
                alt: "Report-Delivery-Options-Image-Reference",
            },
            {
                type: "text",
                content:
                    "Click on Next. Now, review the configuration of the Cost and Usage Report. Once satisfied, click on Create Report.",
            },
        ],
    },
};

export default onboardingFormConfig;
