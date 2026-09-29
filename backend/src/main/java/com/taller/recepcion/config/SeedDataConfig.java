package com.taller.recepcion.config;

import com.taller.recepcion.users.Role;
import com.taller.recepcion.users.UserAccount;
import com.taller.recepcion.users.UserAccountRepository;
import com.taller.recepcion.branches.Branch;
import com.taller.recepcion.branches.BranchRepository;
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
      BranchRepository branches,
      PasswordEncoder passwordEncoder,
      @Value("${app.seed.owner-name}") String ownerName,
      @Value("${app.seed.owner-email}") String ownerEmail,
      @Value("${app.seed.owner-password}") String ownerPassword) {
    return args -> {
      Branch branch = branches.findByCode("MAIN").orElseGet(() -> {
        Branch created = new Branch();
        created.setCode("MAIN");
        created.setName("Taller principal");
        return branches.save(created);
      });
      if (users.existsByEmailIgnoreCase(ownerEmail)) {
        users.findByEmailIgnoreCase(ownerEmail).ifPresent(existing -> {
          existing.setBranch(branch);
          existing.setRole(Role.ADMINISTRATOR);
          users.save(existing);
        });
        return;
      }

      UserAccount owner = new UserAccount();
      owner.setName(ownerName);
      owner.setEmail(ownerEmail.toLowerCase());
      owner.setPasswordHash(passwordEncoder.encode(ownerPassword));
      owner.setRole(Role.ADMINISTRATOR);
      owner.setBranch(branch);
      owner.setActive(true);
      users.save(owner);
    };
  }
}
