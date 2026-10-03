package dio.budgeting.application;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
  String transcribe(MultipartFile audio) throws IOException;

  byte[] synthesize(String text);
}