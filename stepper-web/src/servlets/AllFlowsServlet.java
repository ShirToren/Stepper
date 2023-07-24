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
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.GSON_INSTANCE;

@WebServlet("/all-flows")
public class AllFlowsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        List<FlowDefinitionDTO> flowDefinitionsList;
        synchronized (getServletContext()) {
            flowDefinitionsList = manager.getAllFlowDefinitionsInStepper();
        }
        String jsonResponse = GSON_INSTANCE.toJson(flowDefinitionsList);
        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
