# Segurança, auditoria e MCP

## Segurança implementada

- JWT Bearer via Spring Security OAuth2 Resource Server. O issuer é configurado por `JWT_ISSUER_URI`; sem issuer, todas as requisições são negadas.
- Sessões HTTP stateless e CSRF desativado para a API baseada em bearer token.
- A chave da OpenAI vem de `OPENAI_API_KEY`; não é solicitada nem gravada no home do processo.
- Limite de multipart de 20 MB, nomes de arquivo não são usados como storage key e extensões são allowlisted.
- Timeout de 5 segundos para conexão e 90 segundos para leitura no adaptador direto de voz.
- Logs de Spring AI em INFO e falhas de auditoria guardam somente o tipo da exceção, não mensagem/prompt.

O resource server valida assinatura e issuer usando OIDC discovery/JWKS. A configuração atual exige autenticação, mas não aplica scopes/roles por rota nem valida uma audience específica. Para operação bancária, defina audience e claims com o IdP e autorizações por caso de uso antes de produção. Credenciais MySQL no `compose.yml` são exclusivamente de desenvolvimento.

## Auditoria e dados pessoais

Cada upload nos endpoints de transcrição persiste instante UTC, identidade do JWT, nome original sanitizado, tipo/tamanho, canal do header `X-Client-Channel`, status, SHA-256 e storage key. `/transactions/ai` associa transações criadas pelas tools a `audio_operation_audit` por `audio_transaction_link`. A API não persiste o texto transcrito no registro de auditoria. O arquivo vai ao diretório configurado em `AUDIO_STORAGE_PATH`.

`LocalAudioStorage` é apropriado para desenvolvimento ou volume persistente controlado, não para réplicas efêmeras/distribuídas. Antes de produção, use bucket privado com criptografia gerenciada (KMS), IAM de privilégio mínimo, versionamento conforme política, backup e lifecycle. Implemente uma política documentada de retenção e exclusão que remova metadados, vínculos e objeto; hoje não há endpoint de exclusão nem mecanismo de expurgo automático.

LGPD exige também base legal/consentimento conforme o caso, transparência ao titular, minimização, controle de acesso, processo de atendimento e avaliação de operador/provedor. Esses controles organizacionais e a criptografia de disco/banco/bucket devem ser definidos no ambiente; não são simulados por criptografia caseira na aplicação. O tráfego externo deve usar TLS e segredos devem ser entregues por secret manager.

## Rate limiting e observabilidade

Não há rate limiter implementado. Coloque limite por identidade/cliente e limites mais estritos para upload/custo de IA em gateway ou ingress com store compartilhado entre réplicas. Um contador em memória não atende o cenário distribuído. Adicione quotas diárias, limite de concorrência, proteção contra abuso e política de `429` alinhada ao cliente.

Métricas e tracing distribuído também não estão configurados. Recomenda-se Actuator/Micrometer, OpenTelemetry, correlação por request/audit ID e métricas de latência/erro/custo sem registrar conteúdo de áudio, token ou prompt financeiro.

## Integrações com OpenFeign

Nenhum serviço antifraude/câmbio nem seus contratos existem no projeto, então nenhum endpoint ou cliente foi inventado. Quando os contratos forem fornecidos, crie clients Feign separados por integração e configure connect/read timeout, retry apenas para operações idempotentes, circuit breaker, bulkhead, fallback conservador e métricas. Não use fallback permissivo em decisão antifraude ou conversão financeira; falha deve ser explícita e rastreável.

## MCP e interoperabilidade

MCP Server ainda não está implementado. A recomendação é publicá-lo como processo/módulo independente que exponha tools pequenas e versionadas, por exemplo as tools de transação já usadas pelo chat e uma tool de transcrição. Clientes Node, PHP ou Python falariam MCP sem depender das bibliotecas Spring; o servidor chamaria os contratos HTTP autenticados da API. Não exponha credenciais OpenAI ao cliente nem permita que o MCP contorne autorização/auditoria.

Antes de publicar tools, defina autenticação do transporte MCP, autorização por usuário, limites de payload/áudio, idempotência, timeout, auditoria de chamada, versão de schema e política de dados. Prefira Streamable HTTP e tokens de curta duração emitidos pelo mesmo IdP. A interface `VoiceService` desacopla o fornecedor dentro desta API; para outras linguagens, exponha o serviço de voz via HTTP ou uma tool MCP, não o bean Java.

## Verificação

- `./gradlew test`: testes locais, incluindo armazenamento/hash e associação condicional de transação.
- `OpenAiTranscriptionModelIT` e `OpenAiSpeechModelIT`: opt-in com `OPENAI_API_KEY`, banco disponível e arquivos de áudio de teste.
- Teste JWT, scopes/audience, rate limit, retenção e storage cifrado ainda precisam de ambientes de integração dedicados antes do deploy.
