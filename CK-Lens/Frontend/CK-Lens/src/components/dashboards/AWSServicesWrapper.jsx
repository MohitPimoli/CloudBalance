import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Stack,
  Typography,
  Autocomplete,
  TextField,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import EC2 from "../awsservices/EC2";
import RDS from "../awsservices/RDS";
import ASG from "../awsservices/ASG";
import { House } from "lucide-react";
import { useSelector } from "react-redux";
import { fetchAccounts } from "../../services/awsServiceApis";
import AwsAccountSelect from "../utils/AwsAccountSelect";

const AWSServiceWrapper = () => {
  const [selectedService, setSelectedService] = useState("EC2");
  const [selectedAccount, setSelectedAccount] = useState("");
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);
  const id = user?.id;

  const { data: accounts = [], isLoading } = useQuery({
    queryKey: ["accounts", id],
    queryFn: () => fetchAccounts(),
    enabled: !!id,
  });

  const renderComponent = (selectedAccount) => {
    switch (selectedService) {
      case "EC2":
        return <EC2 accountNumber={selectedAccount} />;
      case "RDS":
        return <RDS accountNumber={selectedAccount} />;
      case "ASG":
        return <ASG accountNumber={selectedAccount} />;
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
