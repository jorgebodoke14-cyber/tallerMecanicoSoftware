package com.taller.recepcion.clients;

import com.taller.recepcion.photos.PhotoStorage;
import com.taller.recepcion.photos.PhotoType;
import com.taller.recepcion.photos.PhotoValidator;
import com.taller.recepcion.postal.PostalCatalogFacade;
import com.taller.recepcion.postal.PostalDtos.Address;
import com.taller.recepcion.users.Role;
import com.taller.recepcion.users.UserAccount;
import com.taller.recepcion.users.UserAccountRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application facade: centralizes authorization, branch scope, validation and persistence. */
@Service
public class ClientFacade {
  private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
  private static final Pattern POSTAL_CODE = Pattern.compile("^\\d{5}$");
  private final ClientRepository clients;
  private final UserAccountRepository users;
  private final PhotoValidator photoValidator;
  private final PhotoStorage photoStorage;
  private final PostalCatalogFacade postalCatalog;
  public ClientFacade(ClientRepository clients, UserAccountRepository users, PhotoValidator photoValidator, PhotoStorage photoStorage,
      PostalCatalogFacade postalCatalog) {
    this.clients = clients; this.users = users; this.photoValidator = photoValidator; this.photoStorage = photoStorage; this.postalCatalog = postalCatalog;
  }
  @Transactional
  public ClientResponse register(String actorEmail, ClientRegistrationRequest request) {
    UserAccount actor = authorizedActor(actorEmail);
    validate(request);
    Address address = postalCatalog.requireAddress(request.stateCode(), request.municipalityCode(), request.settlementId(), request.postalCode());
    String email = request.email().trim().toLowerCase(Locale.ROOT);
    String personalPhone = digits(request.personalPhone());
    Long branchId = actor.getBranch().getId();
    if (clients.existsByBranchIdAndEmailIgnoreCase(branchId, email)) throw new IllegalArgumentException("Ya existe un cliente con este email en la sucursal");
    if (clients.existsByBranchIdAndPersonalPhone(branchId, personalPhone)) throw new IllegalArgumentException("Ya existe un cliente con este telefono personal en la sucursal");
    PhotoType photoType = photoValidator.validate(request.photo());
    PhotoStorage.StoredPhoto photo = photoStorage.store(request.photo(), photoType);
    Client client = new Client();
    client.setBranch(actor.getBranch()); client.setFullName(clean(request.fullName())); client.setAlternateContactName(clean(request.alternateContactName()));
    client.setAge(request.age()); client.setBirthDate(request.birthDate()); client.setPersonalPhone(personalPhone); client.setWorkPhone(optionalPhone(request.workPhone()));
    client.setEmail(email); client.setWorkEmail(blankToNull(request.workEmail(), "Email de trabajo")); client.setPhotoKey(photo.storageKey()); client.setPhotoMimeType(photo.mimeType());
    client.setStreet(clean(request.street())); client.setNeighborhood(address.settlement()); client.setMunicipality(address.municipality()); client.setState(address.state()); client.setPostalCode(address.postalCode());
    return ClientResponse.from(clients.save(client));
  }
  @Transactional(readOnly = true)
  public List<ClientSummaryResponse> list(String actorEmail) {
    UserAccount actor = authorizedActor(actorEmail);
    return clients.findAllByBranchIdOrderByCreatedAtDesc(actor.getBranch().getId()).stream()
        .map(ClientSummaryResponse::from)
        .toList();
  }
  private UserAccount authorizedActor(String actorEmail) {
    UserAccount actor = users.findByEmailIgnoreCase(actorEmail).filter(UserAccount::isActive)
        .orElseThrow(() -> new AccessDeniedException("La sesion no esta activa"));
    if (actor.getRole() != Role.ADMINISTRATOR && actor.getRole() != Role.RECEPTIONIST) {
      throw new AccessDeniedException("Solo Administrador del sistema o Recepcionista puede gestionar clientes");
    }
    return actor;
  }
  private void validate(ClientRegistrationRequest r) {
    required(r.fullName(), "Nombre completo", 2, 120); required(r.alternateContactName(), "Contacto alternativo", 2, 120);
    if (r.age() == null || r.age() < 18 || r.age() > 120) throw new IllegalArgumentException("La edad debe estar entre 18 y 120 anos");
    if (r.birthDate() == null || r.birthDate().isAfter(LocalDate.now()) || Period.between(r.birthDate(), LocalDate.now()).getYears() != r.age()) throw new IllegalArgumentException("La fecha de nacimiento no coincide con la edad");
    if (!PHONE.matcher(digits(r.personalPhone())).matches()) throw new IllegalArgumentException("El telefono personal debe tener 10 digitos");
    if (r.workPhone() != null && !r.workPhone().isBlank() && !PHONE.matcher(digits(r.workPhone())).matches()) throw new IllegalArgumentException("El telefono de trabajo debe tener 10 digitos");
    if (r.email() == null || !r.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("El email personal no tiene un formato valido");
    if (r.workEmail() != null && !r.workEmail().isBlank() && !r.workEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("El email de trabajo no tiene un formato valido");
    required(r.street(), "Calle", 2, 150); required(r.neighborhood(), "Colonia", 2, 100); required(r.municipality(), "Municipio", 2, 100); required(r.state(), "Estado", 2, 100);
    if (r.postalCode() == null || !POSTAL_CODE.matcher(r.postalCode()).matches()) throw new IllegalArgumentException("El codigo postal debe tener 5 digitos");
    required(r.stateCode(), "Clave de estado", 2, 2); required(r.municipalityCode(), "Clave de municipio", 1, 3); required(r.settlementId(), "Asentamiento", 3, 140);
  }
  private static void required(String value, String field, int min, int max) { if (value == null || value.trim().length() < min || value.trim().length() > max) throw new IllegalArgumentException(field + " debe tener entre " + min + " y " + max + " caracteres"); }
  private static String clean(String value) { return value.trim().replaceAll("\\s+", " "); }
  private static String digits(String value) { return value == null ? "" : value.replaceAll("\\D", ""); }
  private static String optionalPhone(String value) { return value == null || value.isBlank() ? null : digits(value); }
  private static String blankToNull(String value, String field) { return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT); }
}
