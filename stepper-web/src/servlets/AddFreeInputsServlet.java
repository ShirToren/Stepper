package servlets;

import com.google.gson.*;
import constants.Constants;
import dd.impl.list.ListData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stepper.management.StepperEngineManager;
import utils.FreeInput;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.adapter.FreeInputDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@WebServlet("/add-inputs")
public class AddFreeInputsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StepperEngineManager manager = ServletUtils.getManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // Read the request body
        BufferedReader reader = req.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        GsonBuilder gsonBuilder = new GsonBuilder().enableComplexMapKeySerialization();
        gsonBuilder.registerTypeAdapter(FreeInput.class, new FreeInputDeserializer());
        Gson gson = gsonBuilder.create();

        // Deserialize the JSON to a list
        List<FreeInput> list = Arrays.asList(gson.fromJson(requestBody.toString(), FreeInput[].class));

        // Process the list as needed
        for (FreeInput freeInput : list) {
            synchronized (getServletContext()) {
            manager.addFreeInputToFlowExecution(freeInput.getId(), freeInput.getInputName(), freeInput.getValue());
            }
        }

        try (PrintWriter out = resp.getWriter()) {
            out.print(list.get(0).getId());
            out.flush();
        }
        // Send the response
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

