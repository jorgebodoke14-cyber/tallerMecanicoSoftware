package com.taller.recepcion.orders;

import com.taller.recepcion.orders.OrderDtos.CreateOrderRequest;
import com.taller.recepcion.orders.OrderDtos.UpdateOrderStatusRequest;
import java.time.Year;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceptionOrderService {
  private final ReceptionOrderRepository orders;

  public ReceptionOrderService(ReceptionOrderRepository orders) {
    this.orders = orders;
  }

  public List<ReceptionOrder> list() {
    return orders.findAll();
  }

  public ReceptionOrder getByFolio(String folio) {
    return orders.findByFolioIgnoreCase(folio)
        .orElseThrow(() -> new IllegalArgumentException("Folio no encontrado"));
  }

  @Transactional
  public ReceptionOrder create(CreateOrderRequest request) {
    ReceptionOrder order = new ReceptionOrder();
    order.setFolio(nextFolio());
    order.setClientName(request.clientName());
    order.setClientPhone(request.clientPhone());
    order.setVehicle(request.vehicle());
    order.setPlate(request.plate());
    order.setReportedIssue(request.reportedIssue());
    order.setAssignedMechanic(request.assignedMechanic());
    order.setStatus(OrderStatus.RECEIVED);
    order.setProgress(10);
    return orders.save(order);
  }

  @Transactional
  public ReceptionOrder updateStatus(Long id, UpdateOrderStatusRequest request) {
    ReceptionOrder order = orders.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
    order.setStatus(request.status());
    order.setProgress(request.progress());
    if (request.assignedMechanic() != null) {
      order.setAssignedMechanic(request.assignedMechanic());
    }
    return order;
  }

  private String nextFolio() {
    long sequence = orders.count() + 1;
    return "REC-" + Year.now().getValue() + "-" + String.format("%04d", sequence);
  }
}
