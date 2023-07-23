package servlets;

import constants.Constants;
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
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/definitions-list")
public class DefinitionsListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        User user = userManager.getUsers().get(username);
        //String[] roles = req.getParameterValues("roles");

/*        int version = ServletUtils.getIntParameter(req, Constants.DEFINITION_VERSION_PARAMETER);
        if (version == Constants.INT_PARAMETER_ERROR) {
            return;
        }*/

       // int flowDefinitionsVersion = 0;
        List<FlowDefinitionDTO> flowDefinitionsList;
        synchronized (getServletContext()) {
            //flowDefinitionsVersion = manager.getFlowDefinitionsVersion(Arrays.asList(roles));
            flowDefinitionsList = manager.getFlowDefinitionsByRole(user.getRoles()/*, version*/);
        }
/*        synchronized (getServletContext()){
            flowDefinitionsList = manager.getFlowDefinitionsByRole(Arrays.asList(roles));
        }*/
        //FlowDefinitionsAndVersion cav = new FlowDefinitionsAndVersion(flowDefinitionsList, flowDefinitionsVersion);

        String jsonResponse = GSON_INSTANCE.toJson(flowDefinitionsList);
        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    private static class FlowDefinitionsAndVersion {
        final private List<FlowDefinitionDTO> entries;
        final private int version;

        public FlowDefinitionsAndVersion(List<FlowDefinitionDTO> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }
}
