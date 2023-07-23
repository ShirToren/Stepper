package servlets;

import constants.Constants;
import impl.FlowExecutionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import stepper.management.StepperEngineManager;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@WebServlet("/execute-flow")
public class ExecuteFlowServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        userManager.getUsers().get(username).addExecution();
        String id = req.getParameter("id");


        if (id != null && !id.isEmpty()) {
            synchronized (getServletContext()){
                Runnable task = () -> {
                    manager.executeFlow(id);
                };
                manager.getExecutor().execute(task);
            }
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = resp.getWriter()) {
            out.print("Success");
            out.flush();
        }
    }

    public class MyTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            // Your task logic here
            return "Task completed successfully!";
        }
    }
}

