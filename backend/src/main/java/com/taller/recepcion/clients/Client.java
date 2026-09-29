package com.taller.recepcion.clients;

import com.taller.recepcion.branches.Branch;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "clients", uniqueConstraints = {
    @UniqueConstraint(name = "uk_client_branch_email", columnNames = {"branch_id", "email"}),
    @UniqueConstraint(name = "uk_client_branch_phone", columnNames = {"branch_id", "personal_phone"})
})
public class Client {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "branch_id", nullable = false) private Branch branch;
  @Column(nullable = false) private String fullName;
  @Column(nullable = false) private String alternateContactName;
  @Column(nullable = false) private Integer age;
  @Column(nullable = false) private LocalDate birthDate;
  @Column(name = "personal_phone", nullable = false) private String personalPhone;
  private String workPhone;
  @Column(nullable = false) private String email;
  private String workEmail;
  @Column(nullable = false) private String photoKey;
  @Column(nullable = false) private String photoMimeType;
  @Column(nullable = false) private String street;
  @Column(nullable = false) private String neighborhood;
  @Column(nullable = false) private String municipality;
  @Column(nullable = false) private String state;
  @Column(nullable = false) private String postalCode;
  @Column(nullable = false) private Instant createdAt;
  @PrePersist void onCreate() { createdAt = Instant.now(); }
  public Long getId() { return id; } public Branch getBranch() { return branch; } public void setBranch(Branch v) { branch=v; }
  public String getFullName() { return fullName; } public void setFullName(String v) { fullName=v; }
  public String getAlternateContactName() { return alternateContactName; } public void setAlternateContactName(String v) { alternateContactName=v; }
  public Integer getAge() { return age; } public void setAge(Integer v) { age=v; }
  public LocalDate getBirthDate() { return birthDate; } public void setBirthDate(LocalDate v) { birthDate=v; }
  public String getPersonalPhone() { return personalPhone; } public void setPersonalPhone(String v) { personalPhone=v; }
  public String getWorkPhone() { return workPhone; } public void setWorkPhone(String v) { workPhone=v; } public String getEmail() { return email; } public void setEmail(String v) { email=v; } public String getWorkEmail() { return workEmail; } public void setWorkEmail(String v) { workEmail=v; }
  public void setPhotoKey(String v) { photoKey=v; } public String getPhotoKey() { return photoKey; } public void setPhotoMimeType(String v) { photoMimeType=v; } public String getPhotoMimeType() { return photoMimeType; }
  public void setStreet(String v) { street=v; } public void setNeighborhood(String v) { neighborhood=v; } public void setMunicipality(String v) { municipality=v; } public void setState(String v) { state=v; } public void setPostalCode(String v) { postalCode=v; }
  public Instant getCreatedAt() { return createdAt; }
}
