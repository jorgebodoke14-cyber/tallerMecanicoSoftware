package com.taller.recepcion.photos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalPhotoStorage implements PhotoStorage {
  private final Path photosDirectory;
  public LocalPhotoStorage(@Value("${app.storage.photos-directory}") String photosDirectory) {
    this.photosDirectory = Path.of(photosDirectory).toAbsolutePath().normalize();
  }
  @Override public StoredPhoto store(MultipartFile file, PhotoType type) {
    try {
      Files.createDirectories(photosDirectory);
      String key = UUID.randomUUID() + "." + type.extension();
      Files.copy(file.getInputStream(), photosDirectory.resolve(key));
      return new StoredPhoto(key, type.mimeType());
    } catch (IOException exception) { throw new IllegalStateException("No fue posible guardar la fotografia", exception); }
  }
  @Override public byte[] load(String storageKey) {
    try { return Files.readAllBytes(photosDirectory.resolve(storageKey).normalize()); }
    catch (IOException exception) { throw new IllegalArgumentException("Fotografia no encontrada"); }
  }
}
