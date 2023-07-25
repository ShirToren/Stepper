package servlets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name ="Hello servlet", urlPatterns = "/hello")
public class HelloWorldServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*        JsonObject jsonObject = new JsonObject();
        jsonObject.add("massage", JsonParser.parseString("hello world"));*/
        resp.setContentType("application/json");
        String json = Constants.GSON_INSTANCE.toJson("hello world");
        resp.getWriter().print(json);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
