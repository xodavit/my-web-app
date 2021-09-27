package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.PasswordResetConfirmRequestDto;
import org.example.app.dto.PasswordResetRequestDto;
import org.example.app.dto.RegistrationRequestDto;

import org.example.app.service.UserService;
import org.example.app.util.UserHelper;

import java.io.IOException;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class UserHandler {
    private final UserService service;
    private final Gson gson;

    public void register(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var registrationRequestDto = gson.fromJson(req.getReader(), RegistrationRequestDto.class);
            final var responseDto = service.register(registrationRequestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "login");
            final var loginRequestDto = gson.fromJson(req.getReader(), LoginRequestDto.class);
            final var responseDto = service.login(loginRequestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "reset password");
            final var user = UserHelper.getUser(req);
            final var passwordResetRequestDto = gson.fromJson(req.getReader(), PasswordResetRequestDto.class);
            final var responseDto = service.resetPassword(user, passwordResetRequestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void resetPasswordConfirm(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "reset password confirm");
            final var passwordResetConfirmRequestDto = gson.fromJson(req.getReader(), PasswordResetConfirmRequestDto.class);
            final var responseDto = service.resetPasswordConfirm(passwordResetConfirmRequestDto);
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
