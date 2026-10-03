package dio.budgeting;

import java.io.IOException;

import dio.budgeting.application.VoiceService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import dio.budgeting.infra.audit.AudioAuditService;

@RestController
@RequestMapping("/api")
public class TranscriptionController {
   private final VoiceService voiceService;
   private final AudioAuditService audioAuditService;

   public TranscriptionController(VoiceService voiceService, AudioAuditService audioAuditService) {
      this.voiceService = voiceService;
      this.audioAuditService = audioAuditService;
   }

   @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public String transcribe(
      @RequestParam MultipartFile file,
      @RequestHeader(value = "X-Client-Channel", defaultValue = "api") String channel
   ) throws IOException {
      if (file.isEmpty()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Audio file must not be empty");
      }
      var audit = audioAuditService.recordReceived(file, channel);
      try {
         String transcription = voiceService.transcribe(file);
         audioAuditService.complete(audit);
         return transcription;
      } catch (IOException | RuntimeException exception) {
         audioAuditService.fail(audit, exception);
         throw exception;
      }
   }
   
}
