import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, Stack, Typography } from "@mui/material";
import EC2 from "../awsservices/EC2";
import RDS from "../awsservices/RDS";
import ASG from "../awsservices/ASG";
import { House } from "lucide-react";
import AwsAccountSelect from "../utils/AwsAccountSelect";
import SnackBar from "../utils/SnackBar";

const AWSServiceWrapper = () => {
  const [selectedService, setSelectedService] = useState("EC2");
  const [selectedAccount, setSelectedAccount] = useState("");
  const navigate = useNavigate();

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: "",
    severity: "error",
  });

  const renderComponent = (selectedAccount) => {
    switch (selectedService) {
      case "EC2":
        return (
          <EC2
            accountNumber={selectedAccount}
            snackbar={snackbar}
            setSnackbar={setSnackbar}
          />
        );
      case "RDS":
        return (
          <RDS
            accountNumber={selectedAccount}
            snackbar={snackbar}
            setSnackbar={setSnackbar}
          />
        );
      case "ASG":
        return (
          <ASG
            accountNumber={selectedAccount}
            snackbar={snackbar}
            setSnackbar={setSnackbar}
          />
        );
      default:
        return null;
    }
  };

  return (
    <Box sx={{ mt: 8, ml: 10, pr: 8 }}>
      {/* Breadcrumb */}
      <Stack
        direction="row"
        alignItems="center"
        spacing={1}
        mb={2}
      >
        <House
          color="#1976d2"
          onClick={() => navigate("/")}
          style={{ cursor: "pointer" }}
        />
        <Typography
          color="#1976d2"
          variant="body2"
        >
          {">"}
        </Typography>
        <Typography
          color="#1976d2"
          onClick={() => navigate("/aws-services")}
          style={{ cursor: "pointer" }}
        >
          AWS Service
        </Typography>
        <Typography
          color="#1976d2"
          variant="body2"
        >
          {">"}
        </Typography>
        <Typography
          color="#1976d2"
          variant="body2"
          fontWeight="bold"
        >
          {selectedService}
        </Typography>
      </Stack>

      <SnackBar
        snackbar={snackbar}
        setSnackbar={setSnackbar}
      />

      {/* Service Buttons & Account Select */}
      <Stack
        direction="row"
        justifyContent="space-between"
        width="100%"
        mb={2}
      >
        <Stack
          direction="row"
          spacing={2}
          flexWrap="wrap"
          rowGap={1}
        >
          <Button
            variant={selectedService === "EC2" ? "contained" : "outlined"}
            onClick={() => setSelectedService("EC2")}
          >
            EC2
          </Button>
          <Button
            variant={selectedService === "RDS" ? "contained" : "outlined"}
            onClick={() => setSelectedService("RDS")}
          >
            RDS
          </Button>
          <Button
            variant={selectedService === "ASG" ? "contained" : "outlined"}
            onClick={() => setSelectedService("ASG")}
          >
            ASG
          </Button>
        </Stack>

        <AwsAccountSelect
          selectedAccount={selectedAccount}
          setSelectedAccount={setSelectedAccount}
          label={"Select AWS Account"}
        />
      </Stack>
      <Box
        sx={{
          width: "100%",
          borderBottom: "1px solid lightgray",
          mt: 1,
        }}
      />

      {!!selectedAccount ? renderComponent(selectedAccount) : <></>}
    </Box>
  );
};

export default AWSServiceWrapper;
