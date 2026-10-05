package com.taller.recepcion.postal.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Parses SEPOMEX's pipe-delimited TXT without depending on a remote service at runtime. */
public class SepomexCatalogParser {
  private static final String[] REQUIRED = {"d_codigo", "d_asenta", "d_tipo_asenta", "D_mnpio", "d_estado", "c_estado", "c_mnpio"};

  public ParsedCatalog parse(Path source) throws IOException {
    Instant started = Instant.now();
    Charset charset = detectCharset(source);
    try (BufferedReader reader = Files.newBufferedReader(source, charset)) {
      String header = reader.readLine();
      if (header == null) throw new IllegalArgumentException("El archivo SEPOMEX esta vacio");
      Map<String, Integer> columns = columns(header);
      validateHeader(columns);
      Map<String, State> states = new LinkedHashMap<>();
      Map<String, Municipality> municipalities = new LinkedHashMap<>();
      Map<String, Settlement> settlements = new LinkedHashMap<>();
      int discarded = 0;
      int duplicates = 0;
      int errors = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) continue;
        try {
          String[] values = line.split("\\|", -1);
          String stateCode = required(values, columns, "c_estado", 2);
          String municipalityCode = required(values, columns, "c_mnpio", 3);
          String postalCode = required(values, columns, "d_codigo", 5);
          String stateName = required(values, columns, "d_estado", 0);
          String municipalityName = required(values, columns, "D_mnpio", 0);
          String name = required(values, columns, "d_asenta", 0);
          String type = required(values, columns, "d_tipo_asenta", 0);
          String sepomexId = optional(values, columns, "id_asenta_cpcons");
          String key = stateCode + "|" + municipalityCode + "|" + postalCode + "|" + sepomexId + "|" + normalize(name);
          if (settlements.containsKey(key)) { duplicates++; continue; }
          states.putIfAbsent(stateCode, new State(stateCode, stateName));
          municipalities.putIfAbsent(stateCode + "|" + municipalityCode,
              new Municipality(stateCode, municipalityCode, municipalityName, normalize(municipalityName)));
          settlements.put(key, new Settlement(key, stateCode, municipalityCode, postalCode, name, normalize(name), type,
              optional(values, columns, "d_ciudad"), optional(values, columns, "d_zona"), sepomexId));
        } catch (IllegalArgumentException invalidRow) {
          discarded++;
        } catch (RuntimeException unexpected) {
          errors++;
        }
      }
      if (states.isEmpty() || municipalities.isEmpty() || settlements.isEmpty()) {
        throw new IllegalArgumentException("El archivo SEPOMEX no contiene registros postales validos");
      }
      return new ParsedCatalog(states, municipalities, settlements, new Summary(states.size(), municipalities.size(), settlements.size(), discarded, duplicates, errors, Duration.between(started, Instant.now())));
    }
  }

  private Charset detectCharset(Path source) throws IOException {
    byte[] bytes = Files.readAllBytes(source);
    String utf8 = new String(bytes, StandardCharsets.UTF_8);
    return utf8.indexOf('\uFFFD') >= 0 ? Charset.forName("windows-1252") : StandardCharsets.UTF_8;
  }
  private Map<String, Integer> columns(String header) {
    String[] names = header.replace("\uFEFF", "").split("\\|", -1);
    Map<String, Integer> result = new LinkedHashMap<>();
    for (int index = 0; index < names.length; index++) result.put(names[index].trim(), index);
    return result;
  }
  private void validateHeader(Map<String, Integer> columns) {
    for (String required : REQUIRED) if (!columns.containsKey(required)) throw new IllegalArgumentException("El archivo no tiene el encabezado SEPOMEX requerido: " + required);
  }
  private String required(String[] values, Map<String, Integer> columns, String column, int padding) {
    String value = optional(values, columns, column);
    if (value == null || value.isBlank()) throw new IllegalArgumentException("Columna obligatoria vacia: " + column);
    String clean = clean(value);
    if (padding > 0) {
      if (!clean.matches("\\d+")) throw new IllegalArgumentException("Clave geografica invalida: " + column);
      return String.format("%" + padding + "s", clean).replace(' ', '0');
    }
    return clean;
  }
  private String optional(String[] values, Map<String, Integer> columns, String column) {
    Integer index = columns.get(column);
    return index == null || index >= values.length || values[index].isBlank() ? null : clean(values[index]);
  }
  private static String clean(String value) { return value.trim().replaceAll("\\s+", " "); }
  public static String normalize(String value) {
    return Normalizer.normalize(clean(value), Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
  }

  public record State(String code, String name) {}
  public record Municipality(String stateCode, String code, String name, String normalizedName) {}
  public record Settlement(String id, String stateCode, String municipalityCode, String postalCode, String name,
                           String normalizedName, String type, String city, String zone, String sepomexSettlementId) {}
  public record Summary(int states, int municipalities, int settlements, int discarded, int duplicates, int errors, Duration elapsed) {}
  public record ParsedCatalog(Map<String, State> states, Map<String, Municipality> municipalities,
                              Map<String, Settlement> settlements, Summary summary) {}
}
