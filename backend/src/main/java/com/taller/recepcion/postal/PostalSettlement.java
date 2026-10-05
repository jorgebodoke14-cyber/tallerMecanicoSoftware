package com.taller.recepcion.postal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "postal_settlements",
    uniqueConstraints = @UniqueConstraint(name = "uk_postal_settlement_identity",
        columnNames = {"state_code", "municipality_code", "postal_code", "name", "type"}),
    indexes = {
        @Index(name = "idx_postal_settlement_municipality", columnList = "state_code,municipality_code,name"),
        @Index(name = "idx_postal_settlement_postal_code", columnList = "postal_code")
    })
public class PostalSettlement {
  @Id @Column(length = 140, updatable = false) private String id;
  @Column(name = "state_code", nullable = false, length = 2) private String stateCode;
  @Column(name = "municipality_code", nullable = false, length = 3) private String municipalityCode;
  @Column(name = "postal_code", nullable = false, length = 5) private String postalCode;
  @Column(nullable = false, length = 180) private String name;
  @Column(nullable = false, length = 180) private String normalizedName;
  @Column(nullable = false, length = 100) private String type;
  @Column(length = 160) private String city;
  @Column(length = 60) private String zone;
  @Column(length = 30) private String sepomexSettlementId;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumns(value = {
      @JoinColumn(name = "state_code", referencedColumnName = "state_code", insertable = false, updatable = false),
      @JoinColumn(name = "municipality_code", referencedColumnName = "municipality_code", insertable = false, updatable = false)
  }, foreignKey = @ForeignKey(name = "fk_postal_settlement_municipality"))
  private PostalMunicipality municipality;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getStateCode() { return stateCode; }
  public void setStateCode(String stateCode) { this.stateCode = stateCode; }
  public String getMunicipalityCode() { return municipalityCode; }
  public void setMunicipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; }
  public String getPostalCode() { return postalCode; }
  public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getNormalizedName() { return normalizedName; }
  public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }
  public String getZone() { return zone; }
  public void setZone(String zone) { this.zone = zone; }
  public String getSepomexSettlementId() { return sepomexSettlementId; }
  public void setSepomexSettlementId(String sepomexSettlementId) { this.sepomexSettlementId = sepomexSettlementId; }
}
