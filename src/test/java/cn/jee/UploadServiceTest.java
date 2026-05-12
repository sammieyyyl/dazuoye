package cn.jee;

import cn.jee.service.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UploadServiceTest {

  @Test
  void saveAndDeleteFile_inUploadsDirectory() throws IOException {
    UploadService uploadService = new UploadService();

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "a b.txt",
      "text/plain",
      "hello".getBytes()
    );

    String stored = uploadService.saveToUploads(file);
    assertNotNull(stored);
    assertFalse(stored.isBlank());

    Path saved = uploadService.getUploadRoot().resolve(stored);
    assertTrue(Files.exists(saved));

    boolean deleted = uploadService.deleteFromUploads(stored);
    assertTrue(deleted);
    assertFalse(Files.exists(saved));
  }
}
