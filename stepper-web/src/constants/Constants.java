package constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";
    public static final String EXECUTION_ID_PARAMETER = "executionID";
    public final static Gson GSON_INSTANCE = new Gson();
    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;
    public static final String DEFINITION_VERSION_PARAMETER = "definitionVersion";
}
