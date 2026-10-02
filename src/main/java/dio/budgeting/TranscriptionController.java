package dio.budgeting;

import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api")
public class TranscriptionController {
   private final TranscriptionModel transcriptionModel;

   public TranscriptionController(TranscriptionModel transcriptionModel) {
      this.transcriptionModel = transcriptionModel;
   }

   @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public String transcribe(@RequestParam MultipartFile file) {
      var resource = file.getResource();
      return transcriptionModel.transcribe(resource);
   }
   
}
