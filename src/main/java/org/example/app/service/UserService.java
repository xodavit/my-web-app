package org.example.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.app.dto.*;
import org.example.app.exception.PasswordNotMatchesException;
import org.example.app.exception.RegistrationException;
import org.example.app.exception.UnsupportedPasswordResetConfirmException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.UserRepository;
import org.example.framework.security.*;
import org.example.framework.util.KeyValue;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.logging.Level;
@Log
@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
  private final UserRepository repository;
  private final JpaTransactionTemplate transactionTemplate;
  private final PasswordEncoder passwordEncoder;
  private final StringKeyGenerator keyGenerator;

  @Override
  public Authentication authenticate(Authentication authentication) {
    final var token = (String) authentication.getPrincipal();

    return repository.findByToken(token)
        // TODO: add user roles
        .map(o -> new TokenAuthentication(o, null, List.of(), true))
        .orElseThrow(AuthenticationException::new);
  }

  @Override
  public AnonymousAuthentication provide() {
    return new AnonymousAuthentication(new User(
        -1,
        "anonymous"
    ));
  }

  public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
    // TODO login:
    //  case-sensitivity: coursar Coursar
    //  cleaning: "  Coursar   "
    //  allowed symbols: [A-Za-z0-9]{2,60}
    //  mis...: Admin, Support, root, ...
    //  мат: ...
    // FIXME: check for nullability
    final var username = requestDto.getUsername().trim().toLowerCase();
    // TODO password:
    //  min-length: 8
    //  max-length: 64
    //  non-dictionary
    final var password = requestDto.getPassword().trim();
    final var hash = passwordEncoder.encode(password);
    final var token = keyGenerator.generateKey();
    final var saved = repository.save(0, username, hash).orElseThrow(RegistrationException::new);

    repository.saveToken(saved.getId(), token);
    return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
  }

  public LoginResponseDto login(LoginRequestDto requestDto) {
    final var username = requestDto.getUsername().trim().toLowerCase();
    final var password = requestDto.getPassword().trim();

    final var result = transactionTemplate.executeInTransaction((entityManager, transaction) -> {
      final var saved = repository.getByUsernameWithPassword(
          entityManager,
          transaction,
          username
      ).orElseThrow(UserNotFoundException::new);

      // TODO: be careful - slow
      if (!passwordEncoder.matches(password, saved.getPassword())) {
        // FIXME: Security issue
        throw new PasswordNotMatchesException();
      }

      final var token = keyGenerator.generateKey();
      repository.saveToken(saved.getId(), token);
      return new KeyValue<>(token, saved);
    });

    // FIXME: Security issue

    final var token = result.getKey();
    final var saved = result.getValue();
    return new LoginResponseDto(saved.getId(), saved.getUsername(), token);
  }

  public int resetPassword(User user, PasswordResetRequestDto requestDto) {
    final var username = user.getUsername().trim().toLowerCase();
    final var password = requestDto.getNewPassword().trim();
    final var encodedPassword = passwordEncoder.encode(password);
    //FIXME ***
    log.log(Level.INFO, encodedPassword);
    return repository.resetPassword(username, encodedPassword);
  }

  public int resetPasswordConfirm(PasswordResetConfirmRequestDto requestDto) {
    final var resetCodeInDB = repository.fingByResetCode(requestDto.getCode());
    if (resetCodeInDB.isPresent() && !resetCodeInDB.get().isActive() && resetCodeInDB.get().getUsername().equals(requestDto.getUsername())) {
      return repository.resetPasswordConfirm(requestDto.getUsername(), requestDto.getCode());
    } else {
      throw new UnsupportedPasswordResetConfirmException("Can`t confirm because code was wrong");
    }
  }
}
