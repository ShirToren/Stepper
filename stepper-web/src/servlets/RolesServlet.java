package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import role.RoleDefinition;
import stepper.management.StepperEngineManager;
import users.User;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/roles")
public class RolesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String userName = req.getParameter("userName");
        Map<String, User> users = userManager.getUsers();
        try (PrintWriter out = resp.getWriter()) {
            List<String> roles = users.get(userName).getRoles();
            String json = GSON_INSTANCE.toJson(roles);
            out.println(json);
            out.flush();
        }
    }
}
