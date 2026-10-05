package com.taller.recepcion.postal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "postal_states")
public class PostalState {
  @Id
  @Column(length = 2, updatable = false)
  private String code;
  @Column(nullable = false, length = 120)
  private String name;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
}
