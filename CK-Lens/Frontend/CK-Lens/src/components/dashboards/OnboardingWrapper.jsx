import React, { useState } from "react";
import { Box } from "@mui/material";
import ThankYouPage from "../../page/ThankYouPage";
import config from "../../config/OnboardingCodeBoxConfig";
import Step1CreateIAMRole from "../../components/onboardingsteps/Step1CreateIAMRole";
import Step2IAMPolicies from "../../components/onboardingsteps/Step2IAMPolicies";
import Step3CreateCUR from "../../components/onboardingsteps/Step3CreateCUR";
import OnboardingNavigationController from "../utils/OnboardingNavigationController";
import { registerAWSAccount } from "../../services/onboardingServiceApis";
import SnackBar from "../utils/SnackBar";

const steps = [Step1CreateIAMRole, Step2IAMPolicies, Step3CreateCUR];
const stepTitles = ["IAM Role", "IAM Policies", "Cost & Usage Report"];

const OnboardingWrapper = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [formValues, setFormValues] = useState({
    arn: "",
    accountName: "",
    accountNumber: "",
    accountRegion: "",
  });

  const [touched, setTouched] = useState({});
  const StepComponent = steps[activeStep];
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [conflictField, setConflictField] = useState(null);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "error",
  });

  const step1FieldsValid = () => {
    return Object.entries(config.fields).every(([key, field]) => {
      const value = formValues[key];
      return !field.required || (value && field.regex.test(value));
    });
  };

  const disableNext = activeStep === 0 && !step1FieldsValid();

  const backLabel =
    activeStep > 0 ? `Back to ${stepTitles[activeStep - 1]}` : "Back";
  const nextLabel =
    activeStep < steps.length - 1
      ? `Next: ${stepTitles[activeStep + 1]}`
      : "Finish";

  const handleNext = async () => {
    if (activeStep === steps.length - 1) {
      try {
        const response = await registerAWSAccount(formValues);
        setSnackbar({
          open: true,
          message: response?.message || "Account registered successfully.",
          severity: "success",
        });
        setIsSubmitted(true);
      } catch (err) {
        if (err?.status === 409) {
          const errorMsg = err.data?.message || "";
          let fieldKey = null;

          if (errorMsg.includes("ARN")) {
            fieldKey = "arn";
            setConflictField("arn");
          } else if (errorMsg.includes("Account Number")) {
            fieldKey = "accountNumber";
            setConflictField("accountNumber");
          }

          if (fieldKey) {
            setTouched((prev) => ({ ...prev, [fieldKey]: true }));
          }

          setSnackbar({
            open: true,
            message: errorMsg || "Account already exists.",
            severity: "error",
          });
          setActiveStep(0);
        } else {
          console.error("Registration failed:", err);
          setSnackbar({
            open: true,
            message: "Something went wrong. Please try again.",
            severity: "error",
          });
        }
      }
    } else {
      setActiveStep((prev) => prev + 1);
    }
  };

  const handleBack = () => {
    if (activeStep > 0) setActiveStep((prev) => prev - 1);
  };

  const handleCancel = () => {
    setFormValues({
      arn: "",
      accountName: "",
      accountNumber: "",
      accountRegion: "",
    });
    setSnackbar({
      open: true,
      message: "Onboarding canceled",
      severity: "info",
    });
    setActiveStep(0);
    setTouched({});
    setIsSubmitted(false);
    setConflictField(null);
  };

  if (isSubmitted) {
    return <ThankYouPage />;
  }

  return (
    <Box sx={{ mt: 4 }}>
      <SnackBar
        setSnackbar={setSnackbar}
        snackbar={snackbar}
      />
      {activeStep === 0 ? (
        <StepComponent
          formValues={formValues}
          setFormValues={setFormValues}
          touched={touched}
          setTouched={setTouched}
          conflictField={conflictField}
        />
      ) : (
        <StepComponent />
      )}
      <OnboardingNavigationController
        onNext={handleNext}
        onBack={handleBack}
        onCancel={handleCancel}
        isLastStep={activeStep === steps.length - 1}
        isFirstStep={activeStep === 0}
        disableNext={disableNext}
        backLabel={backLabel}
        nextLabel={nextLabel}
      />
    </Box>
  );
};

export default OnboardingWrapper;
