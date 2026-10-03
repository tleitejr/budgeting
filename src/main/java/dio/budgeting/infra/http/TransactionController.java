package dio.budgeting.infra.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dio.budgeting.application.ListTransactionByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.application.VoiceService;
import dio.budgeting.domain.Category;
import dio.budgeting.infra.audit.AudioAuditService;
import dio.budgeting.infra.http.request.TransactionRequest;
import dio.budgeting.infra.http.response.TransactionResponse;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
  private final PersistTransactionUseCase persistTransactionUseCase;
  private final ListTransactionByCategoryUseCase listTransactionByCategoryUseCase;
  private final ChatClient chatClient;
  private final VoiceService voiceService;
  private final AudioAuditService audioAuditService;

  public TransactionController(
    PersistTransactionUseCase persistTransactionUseCase,
    ListTransactionByCategoryUseCase listTransactionByCategoryUseCase,
    @Value("classpath:/prompts/system-message.st") Resource systemPrompt,
    ChatClient.Builder chatClientBuilder,
    VoiceService voiceService,
    AudioAuditService audioAuditService
  ) throws IOException {
    this.persistTransactionUseCase = persistTransactionUseCase;
    this.listTransactionByCategoryUseCase = listTransactionByCategoryUseCase;
    this.chatClient = chatClientBuilder
      .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
      .defaultTools(persistTransactionUseCase, listTransactionByCategoryUseCase)
      .build();
    this.voiceService = voiceService;
    this.audioAuditService = audioAuditService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
    var transaction = persistTransactionUseCase.execute(request.toInput());
    return TransactionResponse.from(transaction);
  }

  @GetMapping("/{category}")
  @ResponseStatus(HttpStatus.OK)
  public List<TransactionResponse> readTransactions(@PathVariable Category category) {
    return listTransactionByCategoryUseCase
      .execute(category)
      .stream()
      .map(TransactionResponse::from)
      .toList();
  }

  @PostMapping(
    value = "/ai",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = "audio/mp3"
  )
  public ResponseEntity<Resource> transcribe(
    @RequestParam MultipartFile file,
    @RequestHeader(value = "X-Client-Channel", defaultValue = "unknown") String channel
  ) throws IOException {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    var audit = audioAuditService.recordReceived(file, channel);
    audioAuditService.activate(audit);
    try {
      var userMessage = voiceService.transcribe(file);
      var result = chatClient.prompt().user(userMessage).call().content();
      byte[] audio = voiceService.synthesize(result);
      audioAuditService.complete(audit);
      var resource = new ByteArrayResource(audio);

      return ResponseEntity
        .ok()
        .header(
          HttpHeaders.CONTENT_DISPOSITION,
          ContentDisposition.attachment().filename("audio.mp3").build().toString()
        )
        .body(resource);
    } catch (IOException | RuntimeException exception) {
      audioAuditService.fail(audit, exception);
      throw exception;
    } finally {
      audioAuditService.clearContext();
    }
  }
}
