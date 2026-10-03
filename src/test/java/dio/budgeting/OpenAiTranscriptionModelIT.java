package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import dio.budgeting.application.VoiceService;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiTranscriptionModelIT {
  @Autowired
  VoiceService voiceService;

  @ParameterizedTest
  @CsvSource({
    "recording-1.m4a, 80 reais",
    "recording-2.m4a, 40 reais",
    "recording-3.m4a, 120 reais",
    "recording-4.m4a, 90 reais",
    "recording-5.m4a, 200 reais",
    "recording-6.m4a, 60 reais"
  })
  void should_containExpectedKeywords_when_audioFilesAreProcessed(String fileName, String expectedKeyword) throws Exception {
    var recording = new ClassPathResource("audio/" + fileName);
    var audio = new MockMultipartFile(
      "file",
      fileName,
      "audio/mp4",
      recording.getInputStream().readAllBytes()
    );

    var response = voiceService.transcribe(audio);

    System.out.println(response);
    AssertionsForInterfaceTypes.assertThat(response).contains(expectedKeyword);
  }
}
