package servlets;

import impl.RoleDefinitionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import users.UserManager;
import utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


import static constants.Constants.GSON_INSTANCE;

@WebServlet("/add-roles-to-user")
public class AddRolesToUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        // Read the request body
        BufferedReader reader = req.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String userName = req.getParameter("userName");

        // Deserialize the JSON to a list
        List<RoleDefinitionDTO> list = Arrays.asList(GSON_INSTANCE.fromJson(requestBody.toString(), RoleDefinitionDTO[].class));

        // Process the list as needed

            synchronized (getServletContext()) {
                for (RoleDefinitionDTO roleDefinitionDTO : list) {
                userManager.getUsers().get(userName).addRole(
                        manager.getRoles().get(roleDefinitionDTO.getName()));
/*                        new RoleDefinitionImpl(roleDefinitionDTO.getName(),
                                roleDefinitionDTO.getDescription(),
                                roleDefinitionDTO.getFlows()));*/
                if(roleDefinitionDTO.getName().equals("All Flows")){
                    userManager.getUsers().get(userName).setManager(true);
                }
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
