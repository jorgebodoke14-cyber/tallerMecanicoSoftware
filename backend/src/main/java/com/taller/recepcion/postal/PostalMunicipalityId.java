package com.taller.recepcion.postal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostalMunicipalityId implements Serializable {
  @Column(name = "state_code", length = 2)
  private String stateCode;
  @Column(name = "municipality_code", length = 3)
  private String municipalityCode;

  protected PostalMunicipalityId() {}
  public PostalMunicipalityId(String stateCode, String municipalityCode) {
    this.stateCode = stateCode; this.municipalityCode = municipalityCode;
  }
  public String getStateCode() { return stateCode; }
  public String getMunicipalityCode() { return municipalityCode; }
  @Override public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof PostalMunicipalityId that)) return false;
    return Objects.equals(stateCode, that.stateCode) && Objects.equals(municipalityCode, that.municipalityCode);
  }
  @Override public int hashCode() { return Objects.hash(stateCode, municipalityCode); }
}
