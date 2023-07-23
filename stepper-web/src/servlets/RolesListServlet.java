package servlets;

import impl.RoleDefinitionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/roles-list")
public class RolesListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        Map<String, RoleDefinitionDTO> rolesDTO;
        synchronized (getServletContext()){
           rolesDTO = manager.getRolesDTO();
        }
        try (PrintWriter out = resp.getWriter()) {
            String json = GSON_INSTANCE.toJson(rolesDTO);
            out.println(json);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
