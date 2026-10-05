import { PostalCatalogRepository } from '../repositories/PostalCatalogRepository'

export class PostalCatalogFacade {
  constructor(repository = new PostalCatalogRepository()) { this.repository = repository }
  states() { return this.repository.states() }
  municipalities(stateCode) {
    if (!stateCode) return Promise.resolve([])
    return this.repository.municipalities(stateCode)
  }
  settlements(stateCode, municipalityCode) {
    if (!stateCode || !municipalityCode) return Promise.resolve([])
    return this.repository.settlements(stateCode, municipalityCode)
  }
}
