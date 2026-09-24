package com.taller.recepcion.audit;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditLogRepository auditLogs;

  public AuditController(AuditLogRepository auditLogs) {
    this.auditLogs = auditLogs;
  }

  @GetMapping
  @PreAuthorize("hasRole('OWNER')")
  public List<AuditLog> list() {
    return auditLogs.findAll();
  }
}
