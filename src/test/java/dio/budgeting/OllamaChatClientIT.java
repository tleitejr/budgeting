package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OllamaChatClientIT {
  @Autowired
  OllamaChatModel chatModel;
  
  @Test
  void should_executeSun_when_prompted() {
    var chatClient = ChatClient.builder(chatModel).defaultSystem("Você é um matemático.").build();

    var response = chatClient.prompt("Soma 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado final sem explicações.").call().content();

    System.out.println("Response: " + response);
    AssertionsForInterfaceTypes.assertThat(response).contains("0");
  }
}
