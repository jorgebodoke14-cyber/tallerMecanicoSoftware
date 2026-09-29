package com.taller.recepcion.clients;

public record ClientResponse(Long id, String fullName, String email, Long branchId, String photoUrl) {
  static ClientResponse from(Client client) {
    return new ClientResponse(client.getId(), client.getFullName(), client.getEmail(), client.getBranch().getId(), "/api/client-photos/" + client.getPhotoKey());
  }
}
