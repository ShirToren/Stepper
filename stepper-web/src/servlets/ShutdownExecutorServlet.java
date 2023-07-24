package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet("/shut-down")
public class ShutdownExecutorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        synchronized (getServletContext()) {
            manager.getExecutor().shutdown();
        }
        resp.getWriter().println("Success");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
