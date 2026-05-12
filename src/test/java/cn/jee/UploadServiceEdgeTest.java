package cn.jee;

import cn.jee.service.UploadService;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UploadServiceEdgeTest {

  @Test
  void saveToUploads_throwsWhenMissingFile() {
    UploadService service = new UploadService();
    assertThrows(IOException.class, () -> service.saveToUploads(null));
  }

  @Test
  void deleteFromUploads_returnsFalseForBlankName() throws IOException {
    UploadService service = new UploadService();
    assertFalse(service.deleteFromUploads(" "));
  }
}
