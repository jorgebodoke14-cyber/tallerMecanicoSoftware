package com.taller.recepcion.postal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.taller.recepcion.postal.importer.SepomexCatalogParser;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SepomexCatalogParserTest {
  private final SepomexCatalogParser parser = new SepomexCatalogParser();

  @Test
  void importsFixtureDeduplicatesAndPreservesLeadingZeros() throws Exception {
    Path fixture = Files.createTempFile("sepomex-", ".txt");
    Files.writeString(fixture, "d_codigo|d_asenta|d_tipo_asenta|D_mnpio|d_estado|d_ciudad|c_estado|c_mnpio|id_asenta_cpcons|d_zona\n"
        + "01000|San Angel|Colonia|Alvaro Obregon|Ciudad de Mexico|Ciudad de Mexico|09|010|0001|Urbano\n"
        + "01000|San Angel|Colonia|Alvaro Obregon|Ciudad de Mexico|Ciudad de Mexico|09|010|0001|Urbano\n"
        + "42803|El Llano|Colonia|Tula de Allende|Hidalgo|Tula|13|076|0055|Urbano\n");

    SepomexCatalogParser.ParsedCatalog catalog = parser.parse(fixture);

    assertEquals(2, catalog.summary().states());
    assertEquals(2, catalog.summary().municipalities());
    assertEquals(2, catalog.summary().settlements());
    assertEquals(1, catalog.summary().duplicates());
    assertEquals("01000", catalog.settlements().values().iterator().next().postalCode());
    assertEquals("09", catalog.states().get("09").code());
  }

  @Test
  void rejectsFilesWithoutSepomexHeaders() throws Exception {
    Path fixture = Files.createTempFile("sepomex-invalid-", ".txt");
    Files.writeString(fixture, "estado|municipio\nHidalgo|Tula\n");
    assertThrows(IllegalArgumentException.class, () -> parser.parse(fixture));
  }
}
