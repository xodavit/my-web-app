package org.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetConfirmRequestDto {
    String code;
    String username;
    String password;
    boolean active;
}
