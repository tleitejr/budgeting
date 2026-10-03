package dio.budgeting;

import java.io.IOException;
import java.nio.file.Files;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dio.budgeting.application.VoiceService;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiSpeechModelIT {
  @Autowired
  VoiceService voiceService;

  @Test
  void should_containExpectedKeywords_when_textIsProvided() throws IOException {
    var response = voiceService.synthesize("O valor total do serviço ficou em 80 reais. Posso confirmar o pagamento?");

    AssertionsForInterfaceTypes.assertThat(response).hasSizeGreaterThan(1024);

    var tempFile = Files.createTempFile("AUDIO_", "mp3");
    Files.write(tempFile, response);
    System.out.println(tempFile.toAbsolutePath());
  }
}
