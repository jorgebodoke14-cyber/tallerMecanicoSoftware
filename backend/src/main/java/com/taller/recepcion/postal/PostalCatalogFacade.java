package com.taller.recepcion.postal;

import com.taller.recepcion.postal.PostalDtos.Address;
import com.taller.recepcion.postal.PostalDtos.MunicipalityResponse;
import com.taller.recepcion.postal.PostalDtos.SettlementResponse;
import com.taller.recepcion.postal.PostalDtos.StateResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostalCatalogFacade {
  private final PostalStateRepository states;
  private final PostalMunicipalityRepository municipalities;
  private final PostalSettlementRepository settlements;

  public PostalCatalogFacade(PostalStateRepository states, PostalMunicipalityRepository municipalities,
      PostalSettlementRepository settlements) {
    this.states = states; this.municipalities = municipalities; this.settlements = settlements;
  }

  @Transactional(readOnly = true)
  public List<StateResponse> states() {
    if (states.count() == 0) {
      throw new IllegalArgumentException("El catalogo postal no esta disponible. Importa SEPOMEX antes de registrar clientes.");
    }
    return states.findAllByOrderByNameAsc().stream().map(state -> new StateResponse(state.getCode(), state.getName())).toList();
  }

  @Transactional(readOnly = true)
  public List<MunicipalityResponse> municipalities(String stateCode) {
    requireState(stateCode);
    return municipalities.findByIdStateCodeOrderByNameAsc(stateCode).stream()
        .map(municipality -> new MunicipalityResponse(municipality.getId().getMunicipalityCode(), municipality.getName())).toList();
  }

  @Transactional(readOnly = true)
  public List<SettlementResponse> settlements(String stateCode, String municipalityCode) {
    requireMunicipality(stateCode, municipalityCode);
    return settlements.findByStateCodeAndMunicipalityCodeOrderByNameAsc(stateCode, municipalityCode).stream()
        .map(settlement -> new SettlementResponse(settlement.getId(), settlement.getName(), settlement.getType(), settlement.getPostalCode())).toList();
  }

  @Transactional(readOnly = true)
  public Address requireAddress(String stateCode, String municipalityCode, String settlementId, String postalCode) {
    PostalState state = requireState(stateCode);
    PostalMunicipality municipality = requireMunicipality(stateCode, municipalityCode);
    PostalSettlement settlement = settlements.findById(settlementId)
        .orElseThrow(() -> new IllegalArgumentException("El asentamiento seleccionado no existe en el catalogo postal"));
    if (!settlement.getStateCode().equals(stateCode) || !settlement.getMunicipalityCode().equals(municipalityCode)
        || !settlement.getPostalCode().equals(postalCode)) {
      throw new IllegalArgumentException("La direccion seleccionada no coincide con el catalogo postal");
    }
    return new Address(state.getName(), municipality.getName(), settlement.getName(), settlement.getPostalCode());
  }

  private PostalState requireState(String stateCode) {
    if (states.count() == 0) throw new IllegalArgumentException("El catalogo postal no esta disponible. Importa SEPOMEX antes de registrar clientes.");
    return states.findById(stateCode).orElseThrow(() -> new IllegalArgumentException("El estado seleccionado no existe en el catalogo postal"));
  }

  private PostalMunicipality requireMunicipality(String stateCode, String municipalityCode) {
    return municipalities.findById(new PostalMunicipalityId(stateCode, municipalityCode))
        .orElseThrow(() -> new IllegalArgumentException("El municipio seleccionado no pertenece al estado indicado"));
  }
}
