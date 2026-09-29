package com.taller.recepcion.photos;

import org.springframework.web.multipart.MultipartFile;

/** Provider-neutral port for client photo storage. */
public interface PhotoStorage {
  StoredPhoto store(MultipartFile file, PhotoType type);
  byte[] load(String storageKey);
  record StoredPhoto(String storageKey, String mimeType) {}
}
