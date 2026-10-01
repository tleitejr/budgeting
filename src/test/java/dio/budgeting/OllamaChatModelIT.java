package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OllamaChatModelIT {

    @Autowired
    OllamaApi openAiApi;

    @Test 
    void should_receiveResponse_when_chatModelIsCalled() {
        var options = OllamaChatOptions.builder()
            .model("qwen2.5:0.5b")
            .format("json")
            .build();

        var chatModel = OllamaChatModel
        .builder()
        .ollamaApi(openAiApi)
        .defaultOptions(options)
        .build();

        var response = chatModel.call("Gere um resgistro de budgeting, com descrição de gasto, valor em reais e local.");

        System.out.println("Response: " + response);
        AssertionsForInterfaceTypes.assertThat(response).isNotEmpty();
    }
    
}
