import { apiRequest } from '../services/api'

/** REST access for the local MySQL-backed SEPOMEX catalog. */
export class PostalCatalogRepository {
  states() { return apiRequest('/postal/states') }
  municipalities(stateCode) { return apiRequest(`/postal/states/${encodeURIComponent(stateCode)}/municipalities`) }
  settlements(stateCode, municipalityCode) {
    return apiRequest(`/postal/states/${encodeURIComponent(stateCode)}/municipalities/${encodeURIComponent(municipalityCode)}/settlements`)
  }
}
