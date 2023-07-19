package servlets;

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

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/flow-definition")
public class FlowDefinitionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String flowName = req.getParameter("flowName");
        FlowDefinitionDTO flowDefinitionDTO;
        synchronized (getServletContext()){
            flowDefinitionDTO = manager.showFlowDefinition(flowName);
        }
        String jsonResponse = GSON_INSTANCE.toJson(flowDefinitionDTO);
        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
