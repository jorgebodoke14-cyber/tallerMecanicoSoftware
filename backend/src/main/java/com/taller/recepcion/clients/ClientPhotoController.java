package com.taller.recepcion.clients;

import com.taller.recepcion.photos.PhotoStorage;
import com.taller.recepcion.users.UserAccountRepository;
import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientPhotoController {
  private final ClientRepository clients;
  private final UserAccountRepository users;
  private final PhotoStorage storage;
  public ClientPhotoController(ClientRepository clients, UserAccountRepository users, PhotoStorage storage) { this.clients=clients; this.users=users; this.storage=storage; }
  @GetMapping("/api/client-photos/{storageKey:.+}")
  public ResponseEntity<byte[]> load(@PathVariable String storageKey, Principal principal) {
    Long branchId = users.findByEmailIgnoreCase(principal.getName()).orElseThrow().getBranch().getId();
    Client client = clients.findByPhotoKeyAndBranchId(storageKey, branchId).orElseThrow(() -> new IllegalArgumentException("Fotografia no encontrada"));
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(client.getPhotoMimeType())).body(storage.load(storageKey));
  }
}
