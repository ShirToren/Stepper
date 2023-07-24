package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import role.RoleDefinition;
import stepper.management.StepperEngineManager;
import users.UserManager;
import utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/add-flows-to-role")
public class AddFlowsToRoleServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        // Read the request body
        BufferedReader reader = req.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String roleName = req.getParameter("roleName");

        // Deserialize the JSON to a list
        List<String> list = Arrays.asList(GSON_INSTANCE.fromJson(requestBody.toString(), String[].class));

        // Process the list as needed
        synchronized (getServletContext()) {
            Map<String, RoleDefinition> roles = manager.getRoles();
            RoleDefinition roleDefinition = roles.get(roleName);
            for (String flow : list) {
                roleDefinition.addFlow(flow);
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            out.print("Success");
            out.flush();
        }
        // Send the response
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
