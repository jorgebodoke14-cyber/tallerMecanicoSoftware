package com.taller.recepcion.postal.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SepomexCatalogImporter {
  private static final int BATCH_SIZE = 1_000;
  private final JdbcTemplate jdbc;
  private final SepomexCatalogParser parser = new SepomexCatalogParser();

  public SepomexCatalogImporter(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  @Transactional
  public SepomexCatalogParser.Summary importFile(Path source) throws IOException {
    // Parsing and header validation complete before the current catalog is touched.
    SepomexCatalogParser.ParsedCatalog catalog = parser.parse(source);
    jdbc.update("DELETE FROM postal_settlements");
    jdbc.update("DELETE FROM postal_municipalities");
    jdbc.update("DELETE FROM postal_states");
    batch(new ArrayList<>(catalog.states().values()),
        "INSERT INTO postal_states (code, name) VALUES (?, ?)", (statement, value) -> { statement.setString(1, value.code()); statement.setString(2, value.name()); });
    batch(new ArrayList<>(catalog.municipalities().values()),
        "INSERT INTO postal_municipalities (state_code, municipality_code, name, normalized_name) VALUES (?, ?, ?, ?)",
        (statement, value) -> { statement.setString(1, value.stateCode()); statement.setString(2, value.code()); statement.setString(3, value.name()); statement.setString(4, value.normalizedName()); });
    batch(new ArrayList<>(catalog.settlements().values()),
        "INSERT INTO postal_settlements (id, state_code, municipality_code, postal_code, name, normalized_name, type, city, zone, sepomex_settlement_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        (statement, value) -> { statement.setString(1, value.id()); statement.setString(2, value.stateCode()); statement.setString(3, value.municipalityCode()); statement.setString(4, value.postalCode()); statement.setString(5, value.name()); statement.setString(6, value.normalizedName()); statement.setString(7, value.type()); statement.setString(8, value.city()); statement.setString(9, value.zone()); statement.setString(10, value.sepomexSettlementId()); });
    return catalog.summary();
  }

  private <T> void batch(List<T> values, String sql, StatementBinder<T> binder) {
    for (int start = 0; start < values.size(); start += BATCH_SIZE) {
      List<T> batch = values.subList(start, Math.min(start + BATCH_SIZE, values.size()));
      jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
        @Override public void setValues(PreparedStatement statement, int index) throws SQLException { binder.bind(statement, batch.get(index)); }
        @Override public int getBatchSize() { return batch.size(); }
      });
    }
  }
  @FunctionalInterface private interface StatementBinder<T> { void bind(PreparedStatement statement, T value) throws SQLException; }
}
