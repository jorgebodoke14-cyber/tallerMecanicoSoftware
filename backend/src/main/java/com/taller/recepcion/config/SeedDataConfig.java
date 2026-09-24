package com.taller.recepcion.config;

import com.taller.recepcion.users.Role;
import com.taller.recepcion.users.UserAccount;
import com.taller.recepcion.users.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataConfig {
  @Bean
  CommandLineRunner seedOwnerUser(
      UserAccountRepository users,
      PasswordEncoder passwordEncoder,
      @Value("${app.seed.owner-name}") String ownerName,
      @Value("${app.seed.owner-email}") String ownerEmail,
      @Value("${app.seed.owner-password}") String ownerPassword) {
    return args -> {
      if (users.existsByEmailIgnoreCase(ownerEmail)) {
        return;
      }

      UserAccount owner = new UserAccount();
      owner.setName(ownerName);
      owner.setEmail(ownerEmail.toLowerCase());
      owner.setPasswordHash(passwordEncoder.encode(ownerPassword));
      owner.setRole(Role.OWNER);
      owner.setActive(true);
      users.save(owner);
    };
  }
}
