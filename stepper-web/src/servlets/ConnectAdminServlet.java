package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet("/connect-admin")
public class ConnectAdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        synchronized (getServletContext()) {
            if(manager.isAdminConnected()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                manager.setAdminConnected(true);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }
}
