package dio.budgeting.infra.audit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalAudioStorageTest {
  @TempDir
  Path tempDirectory;

  @Test
  void storesAudioWithDigestAndGeneratedKey() throws Exception {
    byte[] content = "audio-data".getBytes();
    var storage = new LocalAudioStorage(tempDirectory.toString());

    var stored = storage.store(new ByteArrayInputStream(content), "../../recording.MP3");

    assertTrue(stored.key().matches("[0-9a-f-]{36}\\.mp3"));
    assertEquals(content.length, stored.size());
    assertTrue(stored.sha256().matches("[0-9a-f]{64}"));
    assertArrayEquals(content, Files.readAllBytes(tempDirectory.resolve(stored.key())));
  }
}