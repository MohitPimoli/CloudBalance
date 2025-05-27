import React, { useEffect } from "react";
import { Autocomplete, Box, TextField } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { fetchAccounts } from "../../services/awsServiceApis";
import { useSelector } from "react-redux";

const AwsAccountSelect = ({ selectedAccount, setSelectedAccount, label }) => {
  const { user, switchUser } = useSelector((state) => state.auth);
  const isSwitchedByAdmin = useSelector((state) => state?.switch?.isSwitched);
  const { userId } = isSwitchedByAdmin
    ? { userId: switchUser?.id }
    : { userId: user?.id };

  console.log("userId", userId);
  const { data: accounts = [], isLoading } = useQuery({
    queryKey: ["accounts", userId],
    queryFn: () => fetchAccounts(userId),
    enabled: !!userId,
    staleTime: 5 * 60 * 1000,
  });

  useEffect(() => {
    if (accounts.length > 0 && !selectedAccount) {
      setSelectedAccount(accounts[0].accountNumber);
    }
  }, []);

  return (
    <Box minWidth={250}>
      <Autocomplete
        options={accounts}
        getOptionLabel={(option) =>
          `${option.accountHolderName} (${option.accountNumber})`
        }
        value={
          accounts.find((acc) => acc.accountNumber === selectedAccount) || null
        }
        onChange={(e, newValue) =>
          setSelectedAccount(newValue?.accountNumber || "")
        }
        loading={isLoading}
        renderInput={(params) => (
          <TextField
            {...params}
            label={label}
            size="small"
          />
        )}
        disableClearable
        fullWidth
      />
    </Box>
  );
};

export default AwsAccountSelect;
