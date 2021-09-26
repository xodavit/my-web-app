package org.example.framework.listener;

import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebListener;
import jdk.jfr.Percentage;
import org.example.app.handler.CardHandler;
import org.example.app.handler.UserHandler;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.example.app.service.CardService;
import org.example.app.service.UserService;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.servlet.Handler;
import org.example.jdbc.JdbcTemplate;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.example.framework.http.Methods.*;

@ServletSecurity
public class ServletContextLoadDestroyListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContextListener.super.contextInitialized(sce);
    try {
      final var context = sce.getServletContext();

      final var initialContext = new InitialContext();
      final var dataSource = ((DataSource) initialContext.lookup("java:/comp/env/jdbc/db"));
      final var jdbcTemplate = new JdbcTemplate(dataSource);

      final var entityManagerFactory = Persistence.createEntityManagerFactory("default");
      context.setAttribute(ContextAttributes.ENTITY_MANAGER_FACTORY_ATTR, entityManagerFactory);
      final var jpaTransactionTemplate = new JpaTransactionTemplate(entityManagerFactory);

      final var gson = new Gson();

      final var userRepository = new UserRepository(jdbcTemplate);
      final var passwordEncoder = new Argon2PasswordEncoder();
      final var keyGenerator = new Base64StringKeyGenerator(64);
      final var userService = new UserService(userRepository, jpaTransactionTemplate, passwordEncoder, keyGenerator);
      context.setAttribute(ContextAttributes.AUTH_PROVIDER_ATTR, userService);
      context.setAttribute(ContextAttributes.ANON_PROVIDER_ATTR, userService);
      final var userHandler = new UserHandler(userService, gson);

      final var cardRepository = new CardRepository(jdbcTemplate);
      final var cardService = new CardService(cardRepository);
      final var cardHandler = new CardHandler(cardService, gson);

      final var routes = Map.<Pattern, Map<String, Handler>>of(
          Pattern.compile("/cards.getAll"), Map.of(GET, cardHandler::getAll),
          Pattern.compile("/cards.getById"), Map.of(GET, cardHandler::getById),
          Pattern.compile("/cards.order"), Map.of(POST, cardHandler::order),
          Pattern.compile("/cards.blockById"), Map.of(DELETE, cardHandler::blockById),
          Pattern.compile("^/rest/cards/(?<cardId>\\d+)$"), Map.of(GET, cardHandler::getById),
          Pattern.compile("^/rest/users/register$"), Map.of(POST, userHandler::register),
          Pattern.compile("^/rest/users/login$"), Map.of(POST, userHandler::login)
      );
      context.setAttribute(ContextAttributes.ROUTES_ATTR, routes);

    } catch (Exception e) {
      e.printStackTrace();
      throw new ContextInitializationException(e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    ServletContextListener.super.contextDestroyed(sce);
    // TODO: init dependencies

    Optional.ofNullable(sce.getServletContext().getAttribute(ContextAttributes.ENTITY_MANAGER_FACTORY_ATTR))
        .map(o -> ((EntityManagerFactory) o))
        .ifPresent(EntityManagerFactory::close);
  }
}
