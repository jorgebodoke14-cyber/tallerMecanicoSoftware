import { apiRequest } from '../services/api'

/** REST repository; views never depend on HTTP endpoint details. */
export class ClientRepository {
  create(payload) {
    return apiRequest('/clients', { method: 'POST', body: payload })
  }

  list() {
    return apiRequest('/clients')
  }
}
