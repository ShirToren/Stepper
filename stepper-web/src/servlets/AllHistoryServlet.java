package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;

@WebServlet("/all-history")
public class AllHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        List<FlowExecutionDTO> flowExecutionsDTOList;
        synchronized (getServletContext()) {
            flowExecutionsDTOList = manager.getAllFlowExecutionsDTO();
        }

        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(ListData.class, new FilesListSerializer());
        gsonBuilder.registerTypeAdapter(ListData.class, new StringListSerializer());
        Gson gson = gsonBuilder.create();

        String jsonResponse = gson.toJson(flowExecutionsDTOList);

        try (PrintWriter out = resp.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
