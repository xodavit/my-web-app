package org.example.app.handler;


import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.util.logging.Level;


@Log
@RequiredArgsConstructor
public class IndexPageHandler {
    private final Gson gson;

    public void getMainPage(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "getMainPage");
            final var data = "Hello, Amigo ! This is fictive online bank azino 777";
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
