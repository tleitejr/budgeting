package dio.budgeting.infra.audit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalAudioStorage implements AudioStorage {
  private final Path root;

  public LocalAudioStorage(@Value("${app.audio.storage-path:./data/audio}") String storagePath) {
    this.root = Path.of(storagePath).toAbsolutePath().normalize();
  }

  @Override
  public StoredAudio store(InputStream content, String originalFilename) throws IOException {
    String extension = extensionOf(originalFilename);
    String key = UUID.randomUUID() + extension;
    Files.createDirectories(root);
    Path destination = root.resolve(key).normalize();
    if (!destination.startsWith(root)) {
      throw new IOException("Invalid audio storage key");
    }

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      long size = 0;
      try (var output = Files.newOutputStream(Files.createFile(destination))) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = content.read(buffer)) != -1) {
          output.write(buffer, 0, read);
          digest.update(buffer, 0, read);
          size += read;
        }
      }
      return new StoredAudio(key, HexFormat.of().formatHex(digest.digest()), size);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 is not available", exception);
    }
  }

  private String extensionOf(String filename) {
    if (filename == null) {
      return "";
    }
    String normalized = filename.replace('\\', '/');
    String safeName = normalized.substring(normalized.lastIndexOf('/') + 1);
    int dot = safeName.lastIndexOf('.');
    if (dot < 0 || dot == safeName.length() - 1) {
      return "";
    }
    String extension = safeName.substring(dot).toLowerCase();
    return extension.matches("\\.(mp3|wav|m4a|ogg|webm|mp4)") ? extension : "";
  }
}