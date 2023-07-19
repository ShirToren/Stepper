package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import stepper.management.StepperEngineManager;
import users.UserManager;

import static constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String MANAGER_ATTRIBUTE_NAME = "manager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object managerLock = new Object();


    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static StepperEngineManager getManager(ServletContext servletContext) {
        synchronized (managerLock) {
            if (servletContext.getAttribute(MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(MANAGER_ATTRIBUTE_NAME, new StepperEngineManager());
            }
        }
        return (StepperEngineManager) servletContext.getAttribute(MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }
}
