package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolCallingIT {
  @Autowired
  OllamaChatModel chatModel;

  static class MathTools {
    @Tool(description = "Soma dois números inteiros, a e b.")
    public int sum(int a, int b) {
      return a + b;
    }

    @Tool(description = "Subtrai dois números inteiros, a e b.")
    public int diff(int a, int b) {
      return a - b;
    }
  }
  
  @Test
  void should_executeSun_when_prompted() {
    var chatClient = ChatClient
    .builder(chatModel)
    .defaultSystem("Você é um matemático.")
    .defaultTools(new MathTools())
    .build();

    var response = chatClient
    .prompt("Soma 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado final sem explicações.")
    .call()
    .content();

    System.out.println("Response: " + response);
    AssertionsForInterfaceTypes.assertThat(response).contains("0");
  }
}
