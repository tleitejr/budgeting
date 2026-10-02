package dio.budgeting;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class OpenAiConfig {
  public static void ApiKey() throws IOException {
    Path path = Path.of(
			System.getProperty("user.home"),
			".budgeting",
			".openai-api-key"
		);

		Files.createDirectories(path.getParent());

		String apiKey;

		if (Files.exists(path)) {
				apiKey = Files.readString(path).trim();
		} else {
				Console console = System.console();

				if (console == null) {
						throw new IllegalStateException(
								"Execute a aplicação em um terminal."
						);
				}

				apiKey = new String(
						console.readPassword("Digite sua chave da OpenAI: ")
				).trim();

				Files.writeString(
						path,
						apiKey,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING
				);
		}

		System.setProperty("spring.ai.openai.api-key", apiKey);
  }
}
