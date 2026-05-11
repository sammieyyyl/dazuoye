package cn.jee.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class FileNameUtils {
  private FileNameUtils() {
  }

  public static String sanitizeBaseName(String originalFileName) {
    if (originalFileName == null) {
      return "file";
    }
    String normalized = Normalizer.normalize(originalFileName, Normalizer.Form.NFKC);
    String noPath = normalized.replace("\\", "/");
    int lastSlash = noPath.lastIndexOf('/');
    String base = lastSlash >= 0 ? noPath.substring(lastSlash + 1) : noPath;
    String trimmed = base.trim();
    if (trimmed.isBlank()) {
      return "file";
    }
    String safe = trimmed.replaceAll("[^a-zA-Z0-9._-]", "_");
    safe = safe.replaceAll("_+", "_");
    if (safe.length() > 80) {
      safe = safe.substring(0, 80);
    }
    if (safe.isBlank()) {
      return "file";
    }
    return safe;
  }

  public static String extensionLowerCase(String fileName) {
    if (fileName == null) {
      return "";
    }
    int dot = fileName.lastIndexOf('.');
    if (dot < 0 || dot == fileName.length() - 1) {
      return "";
    }
    return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
  }

  public static String uniqueFileName(String baseName) {
    String safe = sanitizeBaseName(baseName);
    String ext = extensionLowerCase(safe);
    String token = UUID.randomUUID().toString().replace("-", "");
    if (ext.isBlank()) {
      return token + "_" + safe;
    }
    String withoutExt = safe.substring(0, safe.length() - ext.length() - 1);
    if (withoutExt.isBlank()) {
      return token + "." + ext;
    }
    return token + "_" + withoutExt + "." + ext;
  }
}
