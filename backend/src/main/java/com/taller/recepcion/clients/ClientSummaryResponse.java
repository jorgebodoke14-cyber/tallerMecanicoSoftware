package com.taller.recepcion.clients;

import java.time.Instant;

public record ClientSummaryResponse(
    Long id, String fullName, String email, String personalPhone, String workPhone, Instant createdAt) {
  static ClientSummaryResponse from(Client client) {
    return new ClientSummaryResponse(
        client.getId(), client.getFullName(), client.getEmail(), client.getPersonalPhone(),
        client.getWorkPhone(), client.getCreatedAt());
  }
}
