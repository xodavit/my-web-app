package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.app.dto.NewsRequestDto;
import org.example.app.service.NewsService;

import java.io.IOException;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class NewsHandler {
    private final NewsService newsService;
    private final Gson gson;
    public void getAllNews(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "getAllNews");
            final var requestDto = gson.fromJson(req.getReader(), NewsRequestDto.class);
            final var responseDto = newsService.getAllNews();
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNews(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "add new News");
            final var requestDto = gson.fromJson(req.getReader(), org.example.app.dto.NewsRequestDto.class);
            final var responseDto = newsService.addNews(requestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
