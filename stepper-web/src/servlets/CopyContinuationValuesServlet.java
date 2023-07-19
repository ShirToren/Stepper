package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet("/copy-continuation-values")
public class CopyContinuationValuesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String sourceID = req.getParameter("sourceID");
        String targetID = req.getParameter("targetID");
        synchronized (getServletContext()) {
            manager.copyContinuationValues(sourceID, targetID);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
