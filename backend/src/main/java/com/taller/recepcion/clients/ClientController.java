package com.taller.recepcion.clients;

import com.taller.recepcion.photos.PhotoStorage;
import com.taller.recepcion.users.UserAccountRepository;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
  private final ClientFacade facade;
  public ClientController(ClientFacade facade) { this.facade = facade; }
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ClientResponse> register(
      Principal principal,
      @RequestParam String fullName, @RequestParam String alternateContactName, @RequestParam Integer age,
      @RequestParam LocalDate birthDate, @RequestParam String personalPhone, @RequestParam(required = false) String workPhone,
      @RequestParam String email, @RequestParam(required = false) String workEmail, @RequestParam String street,
      @RequestParam String neighborhood, @RequestParam String municipality, @RequestParam String state,
      @RequestParam String postalCode, @RequestParam MultipartFile photo) {
    ClientRegistrationRequest request = new ClientRegistrationRequest(fullName, alternateContactName, age, birthDate, personalPhone, workPhone, email, workEmail, street, neighborhood, municipality, state, postalCode, photo);
    return ResponseEntity.status(201).body(facade.register(principal.getName(), request));
  }

  @GetMapping
  public List<ClientSummaryResponse> list(Principal principal) {
    return facade.list(principal.getName());
  }
}
