package com.taller.recepcion.auth;

import com.taller.recepcion.users.Role;
import com.taller.recepcion.users.UserAccountDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDtos {
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

  public record RegisterRequest(
      @NotBlank String name,
      @Email @NotBlank String email,
      @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres") String password,
      @NotNull Role role) {}

  public record ForgotPasswordRequest(@Email @NotBlank String email) {}

  public record ResetPasswordRequest(@NotBlank String token, @Size(min = 8) String password) {}

  public record AuthResponse(String token, UserAccountDto user) {}
}
