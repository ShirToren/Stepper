package servlets;

import constants.Constants;
import impl.FlowDefinitionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;

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
        String[] roles = req.getParameterValues("roles");

        int version = ServletUtils.getIntParameter(req, Constants.DEFINITION_VERSION_PARAMETER);
        if (version == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        int flowDefinitionsVersion = 0;
        List<FlowDefinitionDTO> flowDefinitionsList;
        synchronized (getServletContext()) {
            flowDefinitionsVersion = manager.getFlowDefinitionsVersion(Arrays.asList(roles));
            flowDefinitionsList = manager.getFlowDefinitionsByRole(Arrays.asList(roles), version);
        }
/*        synchronized (getServletContext()){
            flowDefinitionsList = manager.getFlowDefinitionsByRole(Arrays.asList(roles));
        }*/
        FlowDefinitionsAndVersion cav = new FlowDefinitionsAndVersion(flowDefinitionsList, flowDefinitionsVersion);

        String jsonResponse = GSON_INSTANCE.toJson(cav);
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
