package servlets;

import impl.FlowDefinitionDTO;
import jakarta.servlet.ServletException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/assigned-users-by-role")
public class AssignedUsersByRoleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String role = req.getParameter("roleName");

        Map<String, User> users = userManager.getUsers();
        List<String> assignedUsers = new ArrayList<>();
        synchronized (getServletContext()) {
            for (Map.Entry<String, User> entry: users.entrySet()) {
                if(entry.getValue().getRoles().contains(role)) {
                    assignedUsers.add(entry.getKey());
                }
            }
        }
        String jsonResponse = GSON_INSTANCE.toJson(assignedUsers);
        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
