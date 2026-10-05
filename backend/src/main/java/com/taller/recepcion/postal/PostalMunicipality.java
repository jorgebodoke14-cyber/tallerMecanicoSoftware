package com.taller.recepcion.postal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "postal_municipalities")
public class PostalMunicipality {
  @EmbeddedId private PostalMunicipalityId id;
  @Column(nullable = false, length = 160) private String name;
  @Column(nullable = false, length = 160) private String normalizedName;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "state_code", referencedColumnName = "code", insertable = false, updatable = false,
      foreignKey = @ForeignKey(name = "fk_postal_municipality_state"))
  private PostalState state;

  public PostalMunicipalityId getId() { return id; }
  public void setId(PostalMunicipalityId id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getNormalizedName() { return normalizedName; }
  public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }
}
