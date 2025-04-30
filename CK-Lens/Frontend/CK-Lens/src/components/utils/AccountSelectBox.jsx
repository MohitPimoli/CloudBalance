import React, { useState, useEffect, useCallback } from "react";
import {
  Box,
  Typography,
  Checkbox,
  IconButton,
  TextField,
  Paper,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Grid,
} from "@mui/material";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import { useQuery } from "@tanstack/react-query";
import Loading from "../../page/LoadingScreen";
import { fetchAccounts } from "../../services/accountServiceApis";

const AccountIdAssociation = ({ userid, role, onLinkedAccountsChange }) => {
  const [availableAccounts, setAvailableAccounts] = useState([]);
  const [associatedAccounts, setAssociatedAccounts] = useState([]);
  const [selectedAvailable, setSelectedAvailable] = useState([]);
  const [selectedAssociated, setSelectedAssociated] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const {
    data: accounts,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["allAccounts", userid],
    queryFn: () => fetchAccounts(userid),
    enabled: role === "CUSTOMER",
  });

  useEffect(() => {
    if (Array.isArray(accounts)) {
      const assigned = accounts.filter((acc) => acc.linked);
      const available = accounts.filter((acc) => !acc.linked);
      setAssociatedAccounts(assigned);
      setAvailableAccounts(available);
    }
  }, [accounts]);

  const notifyParentOfChanges = useCallback(() => {
    if (onLinkedAccountsChange) {
      onLinkedAccountsChange(associatedAccounts, role);
    }
  }, [associatedAccounts, onLinkedAccountsChange]);

  useEffect(() => {
    notifyParentOfChanges();
  }, [notifyParentOfChanges]);

  const toggleSelection = (account, selectedList, setSelectedList) => {
    const isSelected = selectedList.some(
      (a) => a.accountNumber === account.accountNumber
    );
    if (isSelected) {
      setSelectedList(
        selectedList.filter((a) => a.accountNumber !== account.accountNumber)
      );
    } else {
      setSelectedList([...selectedList, { ...account }]);
    }
  };

  const moveToAssociated = () => {
    setAssociatedAccounts([...associatedAccounts, ...selectedAvailable]);
    setAvailableAccounts(
      availableAccounts.filter(
        (acc) =>
          !selectedAvailable.find(
            (sel) => sel.accountNumber === acc.accountNumber
          )
      )
    );
    setSelectedAvailable([]);
  };

  const moveToAvailable = () => {
    setAvailableAccounts([...availableAccounts, ...selectedAssociated]);
    setAssociatedAccounts(
      associatedAccounts.filter(
        (acc) =>
          !selectedAssociated.find(
            (sel) => sel.accountNumber === acc.accountNumber
          )
      )
    );
    setSelectedAssociated([]);
  };

  const filteredAvailable = availableAccounts.filter((acc) =>
    `${acc.accountHolderName} ${acc.accountNumber}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  if (isLoading) {
    return <Loading message="Fetching Accounts..." />;
  }

  if (isError) {
    return (
      <Box
        textAlign="center"
        p={4}
      >
        <Typography color="error">Failed to fetch accounts</Typography>
      </Box>
    );
  }

  return (
    <Paper sx={{ p: 2 }}>
      <Typography
        variant="h6"
        fontWeight="bold"
        sx={{ mb: 1 }}
      >
        Manage Account Id(s)
      </Typography>

      <Grid
        container
        alignItems="stretch"
        size={{ xs: 6, md: 12, lg: 12 }}
      >
        {/* Available Account IDs */}
        <Grid
          sx={{ height: "100%" }}
          size={{ xs: 12, sm: 5.5 }}
        >
          <Box
            border={1}
            borderColor="grey.300"
            borderRadius={1}
            sx={{
              minHeight: { xs: 300, md: 400 },
              display: "flex",
              flexDirection: "column",
            }}
          >
            {/* Header */}

            <Box
              sx={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                p: 2,
                borderBottom: "1px solid #e0e0e0",
                bgcolor: "#dfe9f5",
              }}
            >
              <Typography fontWeight="bold">
                Choose Account IDs to Associate
              </Typography>
              <Typography
                color="primary"
                sx={{ ml: 1 }}
              >
                {availableAccounts.length} Available
              </Typography>
            </Box>

            <Box p={2}>
              <TextField
                size="small"
                fullWidth
                placeholder="Search"
                value={searchTerm || ""}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </Box>

            <Divider />

            {/* Scrollable List */}

            <Box
              sx={{
                flexGrow: 1,
                px: 2,
                height: 300,
                overflow: "hidden",
                overflowY: "scroll",
              }}
            >
              <List>
                {filteredAvailable.map((account) => (
                  <ListItem
                    key={account.accountNumber}
                    sx={{
                      mb: 0.5,
                      borderRadius: 1,
                      border: "1px solid lightgray",
                      "&:hover": {
                        bgcolor: "#dfe9f5",
                      },
                    }}
                    button={true}
                    onClick={() =>
                      toggleSelection(
                        account,
                        selectedAvailable,
                        setSelectedAvailable
                      )
                    }
                  >
                    <ListItemIcon>
                      <Checkbox
                        checked={selectedAvailable.some(
                          (a) => a.accountNumber === account.accountNumber
                        )}
                      />
                    </ListItemIcon>
                    <ListItemText
                      primary={`${account.accountHolderName} (${account.accountNumber})`}
                    />
                  </ListItem>
                ))}
              </List>
            </Box>
          </Box>
        </Grid>

        {/* Arrow Buttons */}

        <Grid
          sx={{
            display: "flex",
            flexDirection: "column",
            position: "relative",
            top: "20vh",
            height: "100%",
            px: 1,
          }}
        >
          <IconButton onClick={moveToAssociated}>
            <ArrowForwardIosIcon />
          </IconButton>
          <IconButton onClick={moveToAvailable}>
            <ArrowBackIosIcon />
          </IconButton>
        </Grid>

        {/* Associated Account IDs */}

        <Grid
          sx={{ height: "100%" }}
          size={{ xs: 12, sm: 5.5 }}
        >
          <Box
            border={1}
            borderColor="grey.300"
            borderRadius={1}
            sx={{
              minHeight: { xs: 300, md: 400 },
              display: "flex",
              flexDirection: "column",
            }}
          >
            {/* Header */}
            <Box
              sx={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                p: 2,
                borderBottom: "1px solid #e0e0e0",
                bgcolor: "#dfe9f5",
              }}
            >
              <Typography fontWeight="bold">Associated Account IDs</Typography>
              <Typography color="primary">
                {associatedAccounts.length} Added
              </Typography>
            </Box>

            {/* Content */}
            <Box
              sx={{
                height: 370,
                overflow: "hidden",
                overflowY: "scroll",
                flexGrow: 1,
                px: 2,
                py: 1,
              }}
            >
              {associatedAccounts.length === 0 ? (
                <Box
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "text.secondary",
                    height: "100%",
                  }}
                >
                  <Typography
                    variant="h1"
                    fontSize={64}
                  >
                    📁
                  </Typography>
                  <Typography>No Account IDs Added</Typography>
                  <Typography variant="body2">
                    Selected Account IDs will be shown here.
                  </Typography>
                </Box>
              ) : (
                <List dense>
                  {associatedAccounts.map((account) => (
                    <ListItem
                      sx={{
                        mb: 0.5,
                        borderRadius: 1,
                        border: "1px solid lightgray",
                        "&:hover": {
                          bgcolor: "#dfe9f5",
                        },
                      }}
                      key={account.accountNumber}
                      button="true"
                      onClick={() =>
                        toggleSelection(
                          account,
                          selectedAssociated,
                          setSelectedAssociated
                        )
                      }
                    >
                      <ListItemIcon>
                        <Checkbox
                          checked={selectedAssociated.some(
                            (a) => a.accountNumber === account.accountNumber
                          )}
                        />
                      </ListItemIcon>
                      <ListItemText
                        primary={`${account.accountHolderName} (${account.accountNumber})`}
                      />
                    </ListItem>
                  ))}
                </List>
              )}
            </Box>
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default AccountIdAssociation;
