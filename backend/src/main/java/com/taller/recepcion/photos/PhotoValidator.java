package com.taller.recepcion.photos;

import java.util.Locale;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoValidator {
  public static final long MAX_BYTES = 15L * 1024 * 1024;

  public PhotoType validate(MultipartFile file) {
    if (file == null || file.isEmpty()) throw new IllegalArgumentException("La fotografia es obligatoria");
    if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("La fotografia no puede superar 15 MB");
    String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
    PhotoType detected;
    try { detected = detect(file.getBytes()); }
    catch (IOException exception) { throw new IllegalArgumentException("No fue posible leer la fotografia"); }
    if (detected == null) throw new IllegalArgumentException("El archivo no contiene una imagen JPG, PNG o WEBP valida");
    if (!name.endsWith("." + detected.extension()) && !(detected == PhotoType.JPEG && name.endsWith(".jpeg"))) {
      throw new IllegalArgumentException("La extension no coincide con el contenido real de la imagen");
    }
    if (!detected.mimeType().equalsIgnoreCase(file.getContentType())) {
      throw new IllegalArgumentException("El tipo MIME declarado no coincide con el contenido real de la imagen");
    }
    return detected;
  }

  private PhotoType detect(byte[] data) {
    if (data.length >= 3 && (data[0] & 0xff) == 0xff && (data[1] & 0xff) == 0xd8 && (data[2] & 0xff) == 0xff) return PhotoType.JPEG;
    if (data.length >= 8 && (data[0] & 0xff) == 0x89 && data[1] == 0x50 && data[2] == 0x4e && data[3] == 0x47) return PhotoType.PNG;
    if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') return PhotoType.WEBP;
    return null;
  }
}
