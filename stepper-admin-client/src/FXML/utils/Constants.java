package FXML.utils;

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
    public final static String ADD_ROLES = FULL_SERVER_PATH + "/add-roles";
    public final static String REMOVE_ROLES = FULL_SERVER_PATH + "/remove-roles";

    public final static Gson GSON_INSTANCE = new Gson();
}
