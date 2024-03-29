package utils;

import com.google.gson.Gson;

public class Constants {
    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/stepper-web";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String FLOW_DEFINITIONS_LIST = FULL_SERVER_PATH + "/definitions-list";
    public final static String HISTORY = FULL_SERVER_PATH + "/history";
    public final static String FLOW_DEFINITION = FULL_SERVER_PATH + "/flow-definition";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";
    public final static String USER_ROLES = FULL_SERVER_PATH + "/user-roles";
    public final static String FLOW_EXECUTION = FULL_SERVER_PATH + "/flow-execution";
    public final static String COPY_CONTINUATION_VALUES = FULL_SERVER_PATH + "/copy-continuation-values";
    public final static String COPY_FREE_INPUTS_VALUES = FULL_SERVER_PATH + "/copy-free-inputs-values";
    public final static String FLOW_NAME = FULL_SERVER_PATH + "/flow-name";
    public final static String LAST_EXECUTION = FULL_SERVER_PATH + "/last-execution";
    public final static String EXECUTE_FLOW = FULL_SERVER_PATH + "/execute-flow";

    public final static String ADD_INPUTS = FULL_SERVER_PATH + "/add-inputs";
    public final static String ALL_HISTORY = FULL_SERVER_PATH + "/all-history";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    public static final String EXECUTION_ID_PARAMETER = "executionID";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
