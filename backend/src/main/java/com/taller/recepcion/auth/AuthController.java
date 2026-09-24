package com.taller.recepcion.auth;

import com.taller.recepcion.auth.AuthDtos.AuthResponse;
import com.taller.recepcion.auth.AuthDtos.ForgotPasswordRequest;
import com.taller.recepcion.auth.AuthDtos.LoginRequest;
import com.taller.recepcion.auth.AuthDtos.RegisterRequest;
import com.taller.recepcion.auth.AuthDtos.ResetPasswordRequest;
import com.taller.recepcion.users.UserAccountDto;
import com.taller.recepcion.users.UserAccountRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final UserAccountRepository users;

  public AuthController(AuthService authService, UserAccountRepository users) {
    this.authService = authService;
    this.users = users;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/password/forgot")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    authService.forgotPassword(request);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/password/reset")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public UserAccountDto me(Principal principal) {
    return users.findByEmailIgnoreCase(principal.getName())
        .map(UserAccountDto::from)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
  }
}
