package dio.budgeting;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class ChatModelController {
  private final OpenAiChatModel chatModel;
  
  public ChatModelController(OpenAiChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @GetMapping("/chat-model")
  public String chat(String prompt) {
      return this.chatModel.call(prompt);
  }
}
