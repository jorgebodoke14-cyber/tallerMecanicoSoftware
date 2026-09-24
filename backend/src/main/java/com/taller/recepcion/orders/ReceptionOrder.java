package com.taller.recepcion.orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "reception_orders")
public class ReceptionOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String folio;

  @Column(nullable = false)
  private String clientName;

  @Column(nullable = false)
  private String clientPhone;

  @Column(nullable = false)
  private String vehicle;

  @Column(nullable = false)
  private String plate;

  @Column(length = 1200)
  private String reportedIssue;

  private String assignedMechanic;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.RECEIVED;

  @Column(nullable = false)
  private int progress;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getClientPhone() {
    return clientPhone;
  }

  public void setClientPhone(String clientPhone) {
    this.clientPhone = clientPhone;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getPlate() {
    return plate;
  }

  public void setPlate(String plate) {
    this.plate = plate;
  }

  public String getReportedIssue() {
    return reportedIssue;
  }

  public void setReportedIssue(String reportedIssue) {
    this.reportedIssue = reportedIssue;
  }

  public String getAssignedMechanic() {
    return assignedMechanic;
  }

  public void setAssignedMechanic(String assignedMechanic) {
    this.assignedMechanic = assignedMechanic;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }
}
