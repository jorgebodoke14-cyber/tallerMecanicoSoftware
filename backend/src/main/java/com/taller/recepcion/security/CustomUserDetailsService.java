package com.taller.recepcion.security;

import com.taller.recepcion.users.UserAccount;
import com.taller.recepcion.users.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserAccountRepository users;

  public CustomUserDetailsService(UserAccountRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    UserAccount user = users.findByEmailIgnoreCase(username)
        .filter(UserAccount::isActive)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    return User.withUsername(user.getEmail())
        .password(user.getPasswordHash())
        .roles(user.getRole().name())
        .disabled(!user.isActive())
        .build();
  }
}
