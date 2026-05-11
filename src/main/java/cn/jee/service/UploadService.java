package cn.jee.service;

import cn.jee.util.FileNameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {
  private final Path uploadRoot;

  public UploadService() {
    this.uploadRoot = Paths.get("uploads");
  }

  public Path getUploadRoot() {
    return uploadRoot;
  }

  public String saveToUploads(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new IOException("请选择文件");
    }
    String original = file.getOriginalFilename();
    String baseName = StringUtils.hasText(original) ? original : "file";
    String storedName = FileNameUtils.uniqueFileName(baseName);
    Files.createDirectories(uploadRoot);
    Path dest = uploadRoot.resolve(storedName);
    file.transferTo(dest);
    return storedName;
  }

  public boolean deleteFromUploads(String storedName) throws IOException {
    if (storedName == null || storedName.isBlank()) {
      return false;
    }
    String sanitized = FileNameUtils.sanitizeBaseName(storedName);
    Path file = uploadRoot.resolve(sanitized);
    return Files.deleteIfExists(file);
  }
}
