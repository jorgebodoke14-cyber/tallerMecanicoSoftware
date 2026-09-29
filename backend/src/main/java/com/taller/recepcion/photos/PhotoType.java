package com.taller.recepcion.photos;

public enum PhotoType {
  JPEG("image/jpeg", "jpg"), PNG("image/png", "png"), WEBP("image/webp", "webp");
  private final String mimeType;
  private final String extension;
  PhotoType(String mimeType, String extension) { this.mimeType = mimeType; this.extension = extension; }
  public String mimeType() { return mimeType; }
  public String extension() { return extension; }
}
