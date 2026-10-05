package com.taller.recepcion.postal;

public final class PostalDtos {
  private PostalDtos() {}
  public record StateResponse(String code, String name) {}
  public record MunicipalityResponse(String code, String name) {}
  public record SettlementResponse(String id, String name, String type, String postalCode) {}
  public record Address(String state, String municipality, String settlement, String postalCode) {}
}
