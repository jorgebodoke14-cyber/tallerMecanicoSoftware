package com.taller.recepcion.orders;

import com.taller.recepcion.orders.OrderDtos.ClientOrderResponse;
import com.taller.recepcion.orders.OrderDtos.CreateOrderRequest;
import com.taller.recepcion.orders.OrderDtos.OrderResponse;
import com.taller.recepcion.orders.OrderDtos.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceptionOrderController {
  private final ReceptionOrderService service;

  public ReceptionOrderController(ReceptionOrderService service) {
    this.service = service;
  }

  @GetMapping("/api/orders")
  @PreAuthorize("hasAnyRole('OWNER','MANAGER','SECRETARY','MECHANIC','ACCOUNTANT')")
  public List<OrderResponse> list() {
    return service.list().stream().map(OrderResponse::from).toList();
  }

  @PostMapping("/api/orders")
  @PreAuthorize("hasAnyRole('OWNER','MANAGER','SECRETARY')")
  public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return OrderResponse.from(service.create(request));
  }

  @PatchMapping("/api/orders/{id}/status")
  @PreAuthorize("hasAnyRole('OWNER','MANAGER','SECRETARY','MECHANIC')")
  public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
    return OrderResponse.from(service.updateStatus(id, request));
  }

  @GetMapping("/api/public/orders/{folio}")
  public ClientOrderResponse clientLookup(@PathVariable String folio) {
    return ClientOrderResponse.from(service.getByFolio(folio));
  }
}
