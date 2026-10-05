package com.taller.recepcion.postal;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalStateRepository extends JpaRepository<PostalState, String> {
  List<PostalState> findAllByOrderByNameAsc();
}
