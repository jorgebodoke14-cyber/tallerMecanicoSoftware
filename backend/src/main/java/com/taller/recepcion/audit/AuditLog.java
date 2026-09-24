package com.taller.recepcion.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Instant occurredAt = Instant.now();

  @Column(nullable = false)
  private String actor;

  @Column(nullable = false)
  private String method;

  @Column(nullable = false)
  private String path;

  @Column(nullable = false)
  private int status;

  private String ipAddress;

  public void setActor(String actor) {
    this.actor = actor;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public Long getId() {
    return id;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public String getActor() {
    return actor;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public int getStatus() {
    return status;
  }

  public String getIpAddress() {
    return ipAddress;
  }
}
