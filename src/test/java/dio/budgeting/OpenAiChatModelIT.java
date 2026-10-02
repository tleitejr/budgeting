package dio.budgeting;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.ResponseFormat.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {

    @Autowired
    OpenAiApi openAiApi;

    @Test 
    void should_receiveResponse_when_chatModelIsCalled() {
        var options = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .responseFormat(ResponseFormat.builder().type(Type.TEXT).build())
            .build();

        var chatModel = OpenAiChatModel
        .builder()
        .openAiApi(openAiApi)
        .defaultOptions(options)
        .build();

        var response = chatModel.call("Gere um resgistro de budgeting, com descrição de gasto, valor em reais e local.");

        System.out.println("Response: " + response);
        AssertionsForInterfaceTypes.assertThat(response).isNotEmpty();
    }
    
}
