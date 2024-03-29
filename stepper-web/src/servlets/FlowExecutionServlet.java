package servlets;

import com.google.gson.*;
import dd.impl.list.ListData;
import impl.FlowExecutionDTO;
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
import constants.*;
import utils.adapter.FilesListSerializer;
import utils.adapter.StringListSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@WebServlet("/flow-execution")
public class FlowExecutionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        User user = userManager.getUsers().get(username);
        String flowName = req.getParameter("flowName");
        UUID id;
        if (flowName != null && !flowName.isEmpty()) {
            synchronized (getServletContext()) {
                id = manager.createFlowExecution(flowName, username, user.isManager());
            }
            String response = id.toString();
            try (PrintWriter out = resp.getWriter()) {
                out.print(response);
                out.flush();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(ListData.class, new FilesListSerializer());
        gsonBuilder.registerTypeAdapter(ListData.class, new StringListSerializer());
        Gson gson = gsonBuilder.create();

        String id = request.getParameter(Constants.EXECUTION_ID_PARAMETER);
        FlowExecutionDTO executionDTO;
        if (id != null && !id.isEmpty()) {

            synchronized (getServletContext()) {
                executionDTO = manager.getExecutionDTOByUUID(id);
            }


            String jsonResponse = gson.toJson(executionDTO);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
