package dio.budgeting;

import dio.budgeting.application.VoiceService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api")
public class TextToSpeechController {
   private final VoiceService voiceService;

   public TextToSpeechController(VoiceService voiceService) {
      this.voiceService = voiceService;
   }

   record SynthesizeRequest(String text) {}

   @PostMapping(value = "/sinthesize", produces = "audio/mp3")
   public ResponseEntity<Resource> sinthesize(@RequestBody SynthesizeRequest request) {
      byte[] audio = voiceService.synthesize(request.text());
      var resource = new ByteArrayResource(audio);

      return ResponseEntity
      .ok()
      .header(
         HttpHeaders.CONTENT_DISPOSITION,
         ContentDisposition.attachment()
            .filename("audio.mp3")
            .build()
            .toString()
      )
      .body(resource);
   }
   
}
