package com.taller.recepcion.postal;

import com.taller.recepcion.postal.PostalDtos.MunicipalityResponse;
import com.taller.recepcion.postal.PostalDtos.SettlementResponse;
import com.taller.recepcion.postal.PostalDtos.StateResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/postal")
public class PostalCatalogController {
  private final PostalCatalogFacade facade;
  public PostalCatalogController(PostalCatalogFacade facade) { this.facade = facade; }

  @GetMapping("/states")
  public List<StateResponse> states() { return facade.states(); }

  @GetMapping("/states/{stateCode}/municipalities")
  public List<MunicipalityResponse> municipalities(@PathVariable String stateCode) { return facade.municipalities(stateCode); }

  @GetMapping("/states/{stateCode}/municipalities/{municipalityCode}/settlements")
  public List<SettlementResponse> settlements(@PathVariable String stateCode, @PathVariable String municipalityCode) {
    return facade.settlements(stateCode, municipalityCode);
  }
}
