package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
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
      final var user = UserHelper.getUser(req);
      final var data = service.getAllByOwnerId(user.getId());
      resp.setHeader("Content-Type", "application/json");
      resp.getWriter().write(gson.toJson(data));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void getCardById(HttpServletRequest req, HttpServletResponse resp) {
    log.log(Level.INFO, "getById");
  }

  public void transferMoneyToAnotherYourselfCard(HttpServletRequest req, HttpServletResponse resp) {
    log.log(Level.INFO, "transferMoneyToAnotherYourselfCard");
  }

  public void blockById(HttpServletRequest req, HttpServletResponse resp) {
    log.log(Level.INFO, "blockById");
  }
}
