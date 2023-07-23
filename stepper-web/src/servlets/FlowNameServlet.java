package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.Constants;
import dd.impl.list.ListData;
import impl.FlowExecutionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.adapter.FilesListSerializer;
import utils.adapter.StringListSerializer;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/flow-name")
public class FlowNameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String id = req.getParameter(Constants.EXECUTION_ID_PARAMETER);
        String flowName;
        if (id != null && !id.isEmpty()) {
            synchronized (getServletContext()) {
                flowName = manager.getFlowNameByExecutionID(id);
            }

            //String jsonResponse = Constants.GSON_INSTANCE.toJson(flowName);
            try (PrintWriter out = resp.getWriter()) {
                out.print(flowName);
                out.flush();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
