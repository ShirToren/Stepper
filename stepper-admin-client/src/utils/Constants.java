package utils;

import com.google.gson.Gson;

public class Constants {
    public final static int REFRESH_RATE = 2000;
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/stepper_web";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    public final static String STATISTICS = FULL_SERVER_PATH + "/statistics";
    public final static String FLOW_EXECUTIONS_LIST = FULL_SERVER_PATH + "/executions-list";
    public final static String ALL_HISTORY = FULL_SERVER_PATH + "/all-history";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/users-list";
    public final static String ROLES_LIST = FULL_SERVER_PATH + "/roles-list";
    public final static String ADD_ROLES_TO_USER = FULL_SERVER_PATH + "/add-roles-to-user";
    public final static String REMOVE_ROLES_FROM_USER = FULL_SERVER_PATH + "/remove-roles-from-user";
    public final static String REMOVE_FLOWS_FROM_ROLE = FULL_SERVER_PATH + "/remove-flows-from-role";
    public final static String ADD_FLOWS_TO_ROLE = FULL_SERVER_PATH + "/add-flows-to-role";
    public final static String ASSIGNED_USERS_BY_ROLE = FULL_SERVER_PATH + "/assigned-users-by-role";
    public final static String ALL_FLOWS = FULL_SERVER_PATH + "/all-flows";
    public final static String CREATE_ROLE = FULL_SERVER_PATH + "/create-role";
    public final static String FLOW_EXECUTION = FULL_SERVER_PATH + "/flow-execution";
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String CONNECT_ADMIN = FULL_SERVER_PATH + "/connect-admin";

    public static final String EXECUTION_ID_PARAMETER = "executionID";
    public final static String SHUT_DOWN = FULL_SERVER_PATH + "/shut-down";


    public final static Gson GSON_INSTANCE = new Gson();
}
