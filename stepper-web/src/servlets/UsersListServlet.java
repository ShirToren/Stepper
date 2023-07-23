package servlets;

import constants.Constants;
import impl.UserDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import users.User;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/users-list")
public class UsersListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        try (PrintWriter out = response.getWriter()) {
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Map<String, User> users = userManager.getUsers();
            Map<String, UserDTO> finalMap = new HashMap<>();
            for (Map.Entry<String, User> entry : users.entrySet()){
                finalMap.put(entry.getKey(), manager.createUserDTO(entry.getValue()));
            }
            String json = Constants.GSON_INSTANCE.toJson(finalMap);
            out.println(json);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
