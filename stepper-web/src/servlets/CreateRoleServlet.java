package servlets;

import impl.RoleDefinitionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import role.RoleDefinitionImpl;
import stepper.management.StepperEngineManager;
import users.UserManager;
import utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/create-role")
public class CreateRoleServlet extends HttpServlet {
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
        String description = req.getParameter("description");

        // Deserialize the JSON to a list
        List<String> flowsList = Arrays.asList(GSON_INSTANCE.fromJson(requestBody.toString(), String[].class));
        List<String> list = new ArrayList<>(flowsList);
        // Process the list as needed
            synchronized (getServletContext()) {
                manager.getRoles().put(roleName, new RoleDefinitionImpl(roleName, description,
                        list));
            }


        try (PrintWriter out = resp.getWriter()) {
            out.print("Success");
            out.flush();
        }
        // Send the response
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
