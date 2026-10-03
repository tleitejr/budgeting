package dio.budgeting.infra.voice;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

import dio.budgeting.application.VoiceService;

@Service
public class OpenAiDirectVoiceService implements VoiceService {
  private final RestClient client;
  private final String transcriptionModel;
  private final String transcriptionLanguage;
  private final String transcriptionPrompt;
  private final String speechModel;
  private final String speechVoice;
  private final double speechSpeed;
  private final String speechFormat;

  public OpenAiDirectVoiceService(
    RestClient.Builder restClientBuilder,
    @Value("${spring.ai.openai.api-key:}") String apiKey,
    @Value("${app.voice.api-base-url:https://api.openai.com}") String apiBaseUrl,
    @Value("${app.voice.transcription-model:whisper-1}") String transcriptionModel,
    @Value("${app.voice.transcription-language:pt}") String transcriptionLanguage,
    @Value("${app.voice.transcription-prompt:}") String transcriptionPrompt,
    @Value("${app.voice.speech-model:gpt-4o-mini-tts}") String speechModel,
    @Value("${app.voice.speech-voice:nova}") String speechVoice,
    @Value("${app.voice.speech-speed:1.2}") double speechSpeed,
    @Value("${app.voice.speech-format:mp3}") String speechFormat
  ) {
    var requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
    requestFactory.setReadTimeout(Duration.ofSeconds(90));
    var builder = restClientBuilder
      .baseUrl(apiBaseUrl)
      .requestFactory(requestFactory);
    if (apiKey != null && !apiKey.isBlank()) {
      builder.defaultHeader("Authorization", "Bearer " + apiKey);
    }
    this.client = builder.build();
    this.transcriptionModel = transcriptionModel;
    this.transcriptionLanguage = transcriptionLanguage;
    this.transcriptionPrompt = transcriptionPrompt;
    this.speechModel = speechModel;
    this.speechVoice = speechVoice;
    this.speechSpeed = speechSpeed;
    this.speechFormat = speechFormat;
  }

  @Override
  public String transcribe(MultipartFile audio) throws IOException {
    var filePart = new ByteArrayResource(audio.getBytes()) {
      @Override
      public String getFilename() {
        return safeFilename(audio.getOriginalFilename());
      }
    };
    var multipart = new MultipartBodyBuilder();
    multipart.part("file", filePart).contentType(contentTypeOf(audio));
    multipart.part("model", transcriptionModel);
    multipart.part("language", transcriptionLanguage);
    multipart.part("response_format", "json");
    if (!transcriptionPrompt.isBlank()) {
      multipart.part("prompt", transcriptionPrompt);
    }

    var response = client.post()
      .uri("/v1/audio/transcriptions")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(multipart.build())
      .retrieve()
      .body(TranscriptionResponse.class);
    if (response == null || response.text() == null) {
      throw new IllegalStateException("Voice provider returned an empty transcription");
    }
    return response.text();
  }

  @Override
  public byte[] synthesize(String text) {
    return client.post()
      .uri("/v1/audio/speech")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_OCTET_STREAM)
      .body(new SpeechRequest(speechModel, text, speechVoice, speechFormat, speechSpeed))
      .retrieve()
      .body(byte[].class);
  }

  private MediaType contentTypeOf(MultipartFile audio) {
    try {
      return audio.getContentType() == null
        ? MediaType.APPLICATION_OCTET_STREAM
        : MediaType.parseMediaType(audio.getContentType());
    } catch (IllegalArgumentException exception) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  private String safeFilename(String filename) {
    if (filename == null || filename.isBlank()) {
      return "audio";
    }
    String normalized = filename.replace('\\', '/');
    return normalized.substring(normalized.lastIndexOf('/') + 1);
  }

  private record TranscriptionResponse(String text) {}

  private record SpeechRequest(
    String model,
    String input,
    String voice,
    String response_format,
    double speed
  ) {}
}