package dio.budgeting;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class ChatModelController {
  private final OllamaChatModel chatModel;
  
  public ChatModelController(OllamaChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @GetMapping("/chat-model")
  public String chat(String prompt) {
      return this.chatModel.call(prompt);
  }
}
