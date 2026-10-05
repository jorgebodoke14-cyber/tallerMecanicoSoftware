package com.taller.recepcion.postal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.taller.recepcion.postal.PostalDtos.Address;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostalCatalogFacadeTest {
  @Mock private PostalStateRepository states;
  @Mock private PostalMunicipalityRepository municipalities;
  @Mock private PostalSettlementRepository settlements;
  private PostalCatalogFacade facade;

  @BeforeEach void setUp() { facade = new PostalCatalogFacade(states, municipalities, settlements); }

  @Test
  void listsStatesAlphabeticallyFromRepository() {
    PostalState hidalgo = state("13", "Hidalgo");
    when(states.count()).thenReturn(1L);
    when(states.findAllByOrderByNameAsc()).thenReturn(List.of(hidalgo));
    assertEquals("13", facade.states().getFirst().code());
  }

  @Test
  void listsMunicipalitiesForSelectedStateOnly() {
    PostalMunicipality municipality = municipality("13", "076", "Tula de Allende");
    when(states.count()).thenReturn(1L);
    when(states.findById("13")).thenReturn(Optional.of(state("13", "Hidalgo")));
    when(municipalities.findByIdStateCodeOrderByNameAsc("13")).thenReturn(List.of(municipality));
    assertEquals("076", facade.municipalities("13").getFirst().code());
  }

  @Test
  void listsSettlementsForSelectedMunicipalityOnly() {
    when(municipalities.findById(new PostalMunicipalityId("13", "076"))).thenReturn(Optional.of(municipality("13", "076", "Tula de Allende")));
    when(settlements.findByStateCodeAndMunicipalityCodeOrderByNameAsc("13", "076")).thenReturn(List.of(settlement()));
    assertEquals("42803", facade.settlements("13", "076").getFirst().postalCode());
  }

  @Test
  void validatesCoherentAddressAndPreservesLeadingZeroPostalCode() {
    when(states.count()).thenReturn(1L);
    when(states.findById("09")).thenReturn(Optional.of(state("09", "Ciudad de Mexico")));
    when(municipalities.findById(new PostalMunicipalityId("09", "010"))).thenReturn(Optional.of(municipality("09", "010", "Alvaro Obregon")));
    PostalSettlement settlement = settlement(); settlement.setStateCode("09"); settlement.setMunicipalityCode("010"); settlement.setPostalCode("01000");
    when(settlements.findById("settlement-1")).thenReturn(Optional.of(settlement));
    Address address = facade.requireAddress("09", "010", "settlement-1", "01000");
    assertEquals("01000", address.postalCode());
  }

  @Test
  void rejectsManipulatedPostalCode() {
    when(states.count()).thenReturn(1L);
    when(states.findById("13")).thenReturn(Optional.of(state("13", "Hidalgo")));
    when(municipalities.findById(new PostalMunicipalityId("13", "076"))).thenReturn(Optional.of(municipality("13", "076", "Tula de Allende")));
    when(settlements.findById("settlement-1")).thenReturn(Optional.of(settlement()));
    assertThrows(IllegalArgumentException.class, () -> facade.requireAddress("13", "076", "settlement-1", "99999"));
  }

  @Test
  void reportsUnavailableCatalog() {
    when(states.count()).thenReturn(0L);
    assertThrows(IllegalArgumentException.class, () -> facade.municipalities("13"));
  }

  private static PostalState state(String code, String name) { PostalState state = new PostalState(); state.setCode(code); state.setName(name); return state; }
  private static PostalMunicipality municipality(String stateCode, String code, String name) {
    PostalMunicipality municipality = new PostalMunicipality(); municipality.setId(new PostalMunicipalityId(stateCode, code)); municipality.setName(name); municipality.setNormalizedName(name); return municipality;
  }
  private static PostalSettlement settlement() {
    PostalSettlement settlement = new PostalSettlement(); settlement.setId("settlement-1"); settlement.setStateCode("13"); settlement.setMunicipalityCode("076"); settlement.setPostalCode("42803"); settlement.setName("El Llano"); settlement.setType("Colonia"); settlement.setNormalizedName("el llano"); return settlement;
  }
}
