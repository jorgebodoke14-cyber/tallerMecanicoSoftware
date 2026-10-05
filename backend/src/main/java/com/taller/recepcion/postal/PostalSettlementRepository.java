package com.taller.recepcion.postal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalSettlementRepository extends JpaRepository<PostalSettlement, String> {
  List<PostalSettlement> findByStateCodeAndMunicipalityCodeOrderByNameAsc(String stateCode, String municipalityCode);
}
