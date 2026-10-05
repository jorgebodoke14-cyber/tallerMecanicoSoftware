package com.taller.recepcion.postal.importer;

import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.postal.import.enabled", havingValue = "true")
public class SepomexImportRunner implements ApplicationListener<ApplicationReadyEvent> {
  private final SepomexCatalogImporter importer;
  private final String importFile;
  private final ConfigurableApplicationContext context;
  public SepomexImportRunner(SepomexCatalogImporter importer, @Value("${app.postal.import-file:}") String importFile,
      ConfigurableApplicationContext context) {
    this.importer = importer; this.importFile = importFile; this.context = context;
  }
  @Override public void onApplicationEvent(ApplicationReadyEvent event) {
    if (importFile.isBlank()) throw new IllegalArgumentException("Define app.postal.import-file para importar SEPOMEX");
    try {
      SepomexCatalogParser.Summary summary = importer.importFile(Path.of(importFile));
      System.out.printf("SEPOMEX importado: estados=%d municipios=%d asentamientos=%d descartados=%d duplicados=%d errores=%d tiempo=%s%n",
          summary.states(), summary.municipalities(), summary.settlements(), summary.discarded(), summary.duplicates(), summary.errors(), summary.elapsed());
      SpringApplication.exit(context, () -> 0);
    } catch (Exception exception) {
      throw new IllegalStateException("No fue posible importar el catalogo SEPOMEX", exception);
    }
  }
}
