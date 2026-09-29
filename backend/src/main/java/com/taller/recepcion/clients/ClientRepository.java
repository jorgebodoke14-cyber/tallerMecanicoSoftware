package com.taller.recepcion.clients;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
  boolean existsByBranchIdAndEmailIgnoreCase(Long branchId, String email);
  boolean existsByBranchIdAndPersonalPhone(Long branchId, String personalPhone);
  Optional<Client> findByIdAndBranchId(Long id, Long branchId);
  Optional<Client> findByPhotoKeyAndBranchId(String photoKey, Long branchId);
  List<Client> findAllByBranchIdOrderByCreatedAtDesc(Long branchId);
}
