package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatClientIT {
  @Autowired
  OpenAiChatModel chatModel;
  
  @Test
  void should_executeSun_when_prompted() {
    var chatClient = ChatClient.builder(chatModel).defaultSystem("Você é um matemático.").build();

    var response = chatClient.prompt("Soma 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado final sem explicações.").call().content();

    System.out.println("Response: " + response);
    AssertionsForInterfaceTypes.assertThat(response).contains("0");
  }
}
