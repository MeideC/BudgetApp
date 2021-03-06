package com.example.mikko.budgetapplication;

/**
 * Created by Mikko on 13.8.2016.
 */
public class ConstantVariableSettings {

    // Shared Preferences
    public static final String TRANSACTIONS_LAST_LOAD_KEY = "transactions_key";
    public static final String LAST_BACKENDLESS_LOAD_LONG = "last_backendless_load";
    public static final String MONTH_TAB_FRAGMENT_KEY = "month_tab_fragment_key";
    public static final String MONTH_TAB_FRAGMENT_CURRENT_MONTH_INT = "current_month_int";
    public static final String TRANSACTIONS_KEY_STRING = "transactions_key_string";

    // number of months the user can see the history of in the app
    public static final int NUMBER_OF_MONTHS = 14;

    // sending transaction lists across activities
    public static final String TRANSACTION_LIST_PAYMENTS = "transaction_list_payments";
    public static final String TRANSACTION_LIST_INCOMES = "transaction_list_incomes";

    // send user group across activities
    //public static final String SEND_USER_GROUP_NAMES = "send_user_group";
    //public static final String SEND_USER_GROUP_NAME = "send_user_group_name";
    public static final String SEND_USER_GROUPS = "send_user_groups";
    public static final String SEND_USER_GROUP = "send_user_group";

    public static final int VIEW_USER_GROUP_RESULT = 1;
    public static final int CREATE_USER_GROUP_RESULT = 1902;
    public static final int VIEW_PROFILE_RESULT = 2;
}
