package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
import org.example.app.dto.OrderRequestDto;
import org.example.app.service.CardService;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class CardHandler { // Servlet -> Controller -> Service (domain) -> domain
    private final CardService service;
    private final Gson gson;

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "getAll");
            // cards.getAll?ownerId=1
            final var userRequest = UserHelper.getUser(req);
            final var responseDto = service.getAllByOwnerId(userRequest.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getCardById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "getById");
            final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));
            final var user = UserHelper.getUser(req);
            final var data = service.getCardById(user, cardId);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void transferMoneyToAnotherYourselfCard(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "transferMoneyToAnotherYourselfCard");
            final var orderRequestDto = gson.fromJson(req.getReader(), OrderRequestDto.class);
            final var userRequest = UserHelper.getUser(req);
            final var responseDto = service.transferMoneyToAnotherYourselfCard(userRequest, orderRequestDto.getFromCardNumber(), orderRequestDto.getToCardNumber(), orderRequestDto.getMoney());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
//        log.log(Level.INFO, "blockById");
//    }
}
