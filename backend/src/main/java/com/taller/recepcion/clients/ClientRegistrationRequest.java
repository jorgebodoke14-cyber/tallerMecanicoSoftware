package com.taller.recepcion.clients;

import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public record ClientRegistrationRequest(
    String fullName, String alternateContactName, Integer age, LocalDate birthDate,
    String personalPhone, String workPhone, String email, String workEmail,
    String street, String neighborhood, String municipality, String state, String postalCode,
    MultipartFile photo) {}
