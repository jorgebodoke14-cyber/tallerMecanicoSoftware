package com.taller.recepcion.orders;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderDtos {
  public record CreateOrderRequest(
      @NotBlank String clientName,
      @NotBlank String clientPhone,
      @NotBlank String vehicle,
      @NotBlank String plate,
      String reportedIssue,
      String assignedMechanic) {}

  public record UpdateOrderStatusRequest(
      @NotNull OrderStatus status,
      @Min(0) @Max(100) int progress,
      String assignedMechanic) {}

  public record OrderResponse(
      Long id,
      String folio,
      String clientName,
      String clientPhone,
      String vehicle,
      String plate,
      String reportedIssue,
      String assignedMechanic,
      OrderStatus status,
      int progress) {
    public static OrderResponse from(ReceptionOrder order) {
      return new OrderResponse(
          order.getId(),
          order.getFolio(),
          order.getClientName(),
          order.getClientPhone(),
          order.getVehicle(),
          order.getPlate(),
          order.getReportedIssue(),
          order.getAssignedMechanic(),
          order.getStatus(),
          order.getProgress());
    }
  }

  public record ClientOrderResponse(String folio, String vehicle, String status, int progress) {
    public static ClientOrderResponse from(ReceptionOrder order) {
      return new ClientOrderResponse(order.getFolio(), order.getVehicle(), readableStatus(order.getStatus()), order.getProgress());
    }
  }

  private static String readableStatus(OrderStatus status) {
    return switch (status) {
      case RECEIVED -> "Recibido";
      case DIAGNOSIS -> "En diagnostico";
      case WAITING_APPROVAL -> "Esperando autorizacion";
      case PARTS -> "En refacciones";
      case REPAIR -> "En reparacion";
      case QUALITY_TEST -> "En prueba de calidad";
      case READY -> "Listo para entrega";
      case DELIVERED -> "Entregado";
      case CANCELLED -> "Cancelado";
    };
  }
}
