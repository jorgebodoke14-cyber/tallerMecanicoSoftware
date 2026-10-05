package com.taller.recepcion.postal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalMunicipalityRepository extends JpaRepository<PostalMunicipality, PostalMunicipalityId> {
  List<PostalMunicipality> findByIdStateCodeOrderByNameAsc(String stateCode);
}
