package com.taller.recepcion.orders;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionOrderRepository extends JpaRepository<ReceptionOrder, Long> {
  Optional<ReceptionOrder> findByFolioIgnoreCase(String folio);
}
