# API Inteligente com Reconhecimento de Fala para Gestão de Gastos

API Spring Boot para registrar transações financeiras, processar áudio e responder com fala. O chat continua usando Spring AI; transcrição e síntese usam um contrato próprio (`VoiceService`) com adaptador HTTP direto ao provedor OpenAI, facilitando a troca de fornecedor.

## Estado e rotas

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/transactions` | Registra uma transação JSON. |
| `GET` | `/transactions/{category}` | Lista transações por categoria. |
| `POST` | `/transactions/ai` | Recebe `multipart/form-data` no campo `file`, registra auditoria, processa a fala e retorna MP3. |
| `POST` | `/api/transcribe` | Transcreve um áudio e registra auditoria. |
| `POST` | `/api/sinthesize` | Sintetiza o texto JSON recebido e retorna MP3. Mantido com a grafia da rota existente. |
| `GET` | `/api/chat` | Chat via Spring AI `ChatClient`. |
| `GET` | `/api/chat-model` | Chat via modelo OpenAI direto do Spring AI. |

Todas as rotas exigem JWT válido quando `JWT_ISSUER_URI` está configurado. Sem issuer configurado, a API nega todas as requisições. Envie `Authorization: Bearer <token>` e, nos uploads, `X-Client-Channel: mobile` (ou `site`, por exemplo).

As operações de upload gravam o arquivo em `AUDIO_STORAGE_PATH` e os metadados em `audio_operation_audit`: usuário (`sub` do JWT), instante UTC, nome original, tipo, tamanho, canal, SHA-256, storage key e resultado. Transações criadas por tools durante `/transactions/ai` são associadas em `audio_transaction_link`.

## Executar localmente

Requisitos: JDK 25, Docker Compose e credenciais de desenvolvimento. O `compose.yml` contém MySQL para desenvolvimento; suas credenciais não são apropriadas para produção.

```sh
docker compose up -d database
export OPENAI_API_KEY='...'
export JWT_ISSUER_URI='https://seu-idp.example/issuer'
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3307/transaction'
export SPRING_DATASOURCE_USERNAME='app'
export SPRING_DATASOURCE_PASSWORD='app'
export AUDIO_STORAGE_PATH="$PWD/data/audio"
./gradlew bootRun
```

`JWT_ISSUER_URI` deve ser o issuer OIDC que emite os JWTs da aplicação. Para chamadas locais, use um token real emitido por esse issuer. Sem issuer, o modo fail-closed permite iniciar a aplicação, mas nenhuma rota é autorizada.

Exemplo de upload:

```sh
curl -X POST http://localhost:8080/transactions/ai \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'X-Client-Channel: mobile' \
  -F 'file=@./recording.m4a' \
  --output response.mp3
```

Configurações de voz podem ser alteradas com `app.voice.*` no `application.properties`; `OPENAI_API_BASE_URL` permite apontar para um endpoint compatível. O limite padrão de upload é 20 MB. As chamadas ao provedor têm timeout de conexão de 5 s e leitura de 90 s.

## Testes

```sh
./gradlew test
```

A suíte padrão é local e não precisa de uma chave OpenAI. Os testes `OpenAi*IT` de chat/voz são opt-in com `OPENAI_API_KEY` e exigem também banco disponível quando iniciam o contexto Spring. Consulte [arquitetura](docs/architecture.md) e [segurança, auditoria e MCP](docs/security-audit-mcp.md) para decisões e itens ainda pendentes.
