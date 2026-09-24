package com.taller.recepcion.users;

public record UserAccountDto(Long id, String name, String email, Role role, boolean active) {
  public static UserAccountDto from(UserAccount user) {
    return new UserAccountDto(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
  }
}
