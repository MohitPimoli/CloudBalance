package com.cloudbalance.lens.utils;

import lombok.Data;

@Data
public class Constant {

    private Constant() {
    }
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String ACCOUNT_NOT_FOUND = "Account not found with account number:";
    public static final String ACCOUNT_NOT_FOUND_WITH_ACCOUNT_NUMBER = "Account not found with AccountNumber:";
    public static final String ACCOUNT_NOT_FOUND_WITH_ACCOUNT_ID = "Account not found with account id:";
    public static final String USER_NOT_FOUND_WITH_ID = "User not found with id:";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username:";
    public static final String COLUMN_NAME_NOT_FOUND=  "Column Name not found for FieldName:";
    public static final String FIELD_NAME_NOT_FOUND = "Field Name not found for columnName";
}
