package com.taller.recepcion.auth;

import com.taller.recepcion.auth.AuthDtos.AuthResponse;
import com.taller.recepcion.auth.AuthDtos.ForgotPasswordRequest;
import com.taller.recepcion.auth.AuthDtos.LoginRequest;
import com.taller.recepcion.auth.AuthDtos.RegisterRequest;
import com.taller.recepcion.auth.AuthDtos.ResetPasswordRequest;
import com.taller.recepcion.security.JwtService;
import com.taller.recepcion.users.UserAccount;
import com.taller.recepcion.users.UserAccountDto;
import com.taller.recepcion.users.UserAccountRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.taller.recepcion.users.Role;
import java.util.Set;

@Service
public class AuthService {
  private final UserAccountRepository users;
  private final PasswordResetTokenRepository resetTokens;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private static final Set<Role> ASSIGNABLE_ROLES = Set.of(
      Role.ADMINISTRATOR, Role.RECEPTIONIST, Role.MANAGER, Role.SECRETARY,
      Role.MECHANIC, Role.ACCOUNTANT);

  public AuthService(
      UserAccountRepository users,
      PasswordResetTokenRepository resetTokens,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.users = users;
    this.resetTokens = resetTokens;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(String actorEmail, RegisterRequest request) {
    UserAccount actor = users.findByEmailIgnoreCase(actorEmail)
        .filter(UserAccount::isActive)
        .orElseThrow(() -> new BadCredentialsException("Sesion invalida"));
    if (actor.getRole() != Role.ADMINISTRATOR) {
      throw new IllegalArgumentException("Solo el Administrador del sistema puede crear usuarios internos");
    }
    if (!ASSIGNABLE_ROLES.contains(request.role())) {
      throw new IllegalArgumentException("El rol seleccionado no puede asignarse desde este modulo");
    }
    if (users.existsByEmailIgnoreCase(request.email())) {
      throw new IllegalArgumentException("El correo ya esta registrado");
    }

    UserAccount user = new UserAccount();
    user.setName(request.name());
    user.setEmail(request.email().trim().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(request.role());
    user.setBranch(actor.getBranch());
    user.setActive(true);
    users.save(user);

    return new AuthResponse(jwtService.createToken(user), UserAccountDto.from(user));
  }

  public AuthResponse login(LoginRequest request) {
    UserAccount user = users.findByEmailIgnoreCase(request.email())
        .filter(UserAccount::isActive)
        .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new BadCredentialsException("Credenciales invalidas");
    }

    return new AuthResponse(jwtService.createToken(user), UserAccountDto.from(user));
  }

  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    users.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
      PasswordResetToken resetToken = new PasswordResetToken();
      resetToken.setUser(user);
      resetToken.setToken(UUID.randomUUID().toString());
      resetToken.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
      resetTokens.save(resetToken);
      // Hook para enviar email/SMS. El token se persiste para auditoria y restablecimiento.
    });
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    PasswordResetToken token = resetTokens.findByToken(request.token())
        .filter(value -> !value.isUsed())
        .filter(value -> value.getExpiresAt().isAfter(Instant.now()))
        .orElseThrow(() -> new IllegalArgumentException("Token invalido o expirado"));

    UserAccount user = token.getUser();
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    token.setUsed(true);
  }
}
