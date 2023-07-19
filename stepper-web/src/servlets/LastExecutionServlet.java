package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.Constants;
import flow.execution.FlowExecution;
import impl.FlowExecutionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/last-execution")
public class LastExecutionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        Gson gson = gsonBuilder.create();

        FlowExecutionDTO executionDTO;

            synchronized (getServletContext()) {
                FlowExecution flowExecution = manager.getAllFlowExecutionsList().get(0);
                executionDTO = manager.getExecutionDTOByUUID(flowExecution.getUuid().toString());
            }
            String jsonResponse = gson.toJson(executionDTO);
            try (PrintWriter out = resp.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
