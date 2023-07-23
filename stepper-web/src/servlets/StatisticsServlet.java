package servlets;

import constants.Constants;
import impl.StatisticsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/statistics")
public class StatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());

        StatisticsDTO statisticsDTO;

        synchronized (getServletContext()){
            statisticsDTO = manager.createStatisticsDTO();
        }

        String json = Constants.GSON_INSTANCE.toJson(statisticsDTO);

        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
            out.flush();
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
