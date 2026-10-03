package dio.budgeting.infra.audit;

import java.io.IOException;
import java.io.InputStream;

public interface AudioStorage {
  StoredAudio store(InputStream content, String originalFilename) throws IOException;

  record StoredAudio(String key, String sha256, long size) {}
}