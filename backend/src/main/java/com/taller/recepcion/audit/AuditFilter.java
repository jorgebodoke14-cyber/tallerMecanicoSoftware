package com.taller.recepcion.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuditFilter extends OncePerRequestFilter {
  private final AuditLogRepository auditLogs;

  public AuditFilter(AuditLogRepository auditLogs) {
    this.auditLogs = auditLogs;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      if (request.getRequestURI().startsWith("/api/")) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuditLog log = new AuditLog();
        log.setActor(authentication == null ? "anonymous" : authentication.getName());
        log.setMethod(request.getMethod());
        log.setPath(request.getRequestURI());
        log.setStatus(response.getStatus());
        log.setIpAddress(request.getRemoteAddr());
        auditLogs.save(log);
      }
    }
  }
}
