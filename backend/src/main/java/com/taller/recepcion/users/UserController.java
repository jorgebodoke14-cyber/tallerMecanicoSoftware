package com.taller.recepcion.users;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserAccountRepository users;

  public UserController(UserAccountRepository users) {
    this.users = users;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
  public List<UserAccountDto> listUsers() {
    return users.findAll().stream().map(UserAccountDto::from).toList();
  }
}
