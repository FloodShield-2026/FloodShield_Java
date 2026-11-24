# FloodShield Java API

Backend Spring Boot da CitizenTech **FloodShield** — Global Solution FIAP 2026/2.

**Predict. Protect. Save Lives.**

| Artefato | Link |
| --- | --- |
| Repositório | https://github.com/FloodShield-2026/FloodShield_Java |
| **Vídeo da apresentação da Global Solution** | https://youtu.be/osdBjXJQBMQ |
| Vídeo pitch (≤ 5 min) | https://youtu.be/5QRUoqrZJ-A |
| App Mobile (consome esta API) | https://github.com/FloodShield-2026/FloodShield_Mobile |

## Equipe

| Nome | RM |
| --- | --- |
| Bruno Vinicius Barbosa | 566366 |
| Guilherme de Andrade Martini | 566087 |
| Nathan Gonçalves Pereira Mendes | 564666 |
| Raphael Gomes Mancera | 562279 |

## Integração multidisciplinar

Uma solução CitizenTech — cada disciplina com papel claro e link do repositório.

| Disciplina | Papel no FloodShield | Repositório / artefato |
| --- | --- | --- |
| **Java Advanced** (este repo) | API REST do domínio, JWT, Spring AI, RabbitMQ | https://github.com/FloodShield-2026/FloodShield_Java |
| **Mobile Application Development** | App Expo consome esta API (`EXPO_PUBLIC_JAVA_API_URL`) | https://github.com/FloodShield-2026/FloodShield_Mobile |
| **IA / Spring AI** | Plano de ação com RAG da cartilha (`POST /ai/plano-acao`) | Endpoint neste repo + `cartilha/protocolo-enchente.txt` |
| **Advanced Business (.NET)** | Doações / check-in com `Idempotency-Key` | https://github.com/brunovb002/floodshield-doacoes |
| **DevOps** | Azure DevOps (Boards, Repos, Pipelines) + App Service | https://dev.azure.com/RM566366/GS%202026%20-%20FloodShield · API .NET: https://app-floodshield-doacoes.azurewebsites.net/scalar |
| **Database** | Oracle PL/SQL + MongoDB (NoSQL) da CitizenTech | Repo Database (procedures/packages fora deste backend) |

> PDFs / evidências ficam em `docs/` (nunca na raiz do repositório).

## Stack

- Java 21 + Spring Boot 3.4
- Spring Data JPA + **Flyway** (H2 em dev / PostgreSQL em prod)
- Spring Security + JWT (`CIDADAO`, `GESTOR`)
- Bean Validation + `@RestControllerAdvice` (inclui **409** em conflito de unicidade)
- Spring AMQP / RabbitMQ (evento `NivelCritico`)
- **Spring AI** (`ChatModel` + RAG da cartilha local — fallback offline)
- springdoc OpenAPI / Swagger UI
- Paginação nos CRUDs

## Segurança e segredos

- **Nunca** commitar senha real, connection string ou API key.
- Copie `.env.example` → `.env` (`.env` está no `.gitignore`).
- Em produção: defina `JWT_SECRET`, `SPRING_DATASOURCE_*` e, se usar LLM, `OPENAI_API_KEY`.
- Se algum segredo já vazou no histórico do Git: **troque a senha/chave imediatamente** e rode o app só com variáveis de ambiente.

## Como rodar

```bash
# opcional — sobe RabbitMQ
docker compose up -d

mvn spring-boot:run
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Health: http://localhost:8080/actuator/health  

Consumer RabbitMQ:

```bash
RABBITMQ_LISTENER_ENABLED=true mvn spring-boot:run
```

Perfil produção (PostgreSQL):

```bash
SPRING_PROFILES_ACTIVE=prod ^
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/floodshield ^
SPRING_DATASOURCE_USERNAME=... ^
SPRING_DATASOURCE_PASSWORD=... ^
JWT_SECRET=... ^
mvn spring-boot:run
```

## Database (Flyway)

Migrations em `src/main/resources/db/migration/`:

| Migration | Conteúdo |
| --- | --- |
| `V1__create_schema.sql` | Tabelas: usuarios, abrigos, pontos_regua, ocorrencias, leituras, alertas |
| `V2__constraints_and_indexes.sql` | Unique de e-mail / ponto+bairro + índices FK |

Hibernate usa `ddl-auto: validate` — o schema vem do Flyway, não de auto-update.

PL/SQL, packages e MongoDB pertencem à disciplina **Database** (repo separado), não a esta API.

## Usuários de teste

| E-mail | Senha | Papel |
| --- | --- | --- |
| cidadao@floodshield.app | 123456 | CIDADAO |
| gestor@floodshield.app | 123456 | GESTOR |

## Endpoints principais

| Método | Path | Descrição |
| --- | --- | --- |
| POST | `/auth/login` | Login JWT |
| POST | `/auth/register` | Cadastro cidadão |
| GET/POST/PUT/DELETE | `/ocorrencias` | CRUD paginado |
| GET/POST/PUT | `/abrigos` | Abrigos (escrita GESTOR) |
| GET | `/pontos` | Pontos de régua |
| GET/POST | `/leituras` | Leituras + dispara alerta crítico |
| POST | `/ai/plano-acao` | Plano de ação (**Spring AI** + RAG) |
| GET | `/alertas` | Alertas gerados |

## Arquitetura distribuída (requisito 5A)

Quando uma leitura passa do limite do ponto, a API:

1. Publica `NivelCriticoEvent` no exchange RabbitMQ  
2. O consumer grava um `Alerta`  
3. Se o broker estiver offline, o serviço ainda persiste o alerta localmente  

## Plano de ação (`/ai/plano-acao`) — IA na solução

Funcionalidade de domínio (não é chat genérico):

1. Lê ocorrência / nível / bairro  
2. Sugere abrigo mais próximo  
3. Monta plano com **Spring AI** + protocolo em `cartilha/protocolo-enchente.txt`  
4. Sem `OPENAI_API_KEY`, usa RAG local da mesma cartilha  

## Integração com o Mobile

App: https://github.com/FloodShield-2026/FloodShield_Mobile  

```bash
EXPO_PUBLIC_JAVA_API_URL=http://localhost:8080
```

Quando houver URL pública da API Java no Azure (DevOps), o Mobile troca pela URL publicada.

## Vídeo da apresentação da Global Solution

Link público (YouTube): **https://youtu.be/osdBjXJQBMQ**

Apresentação da disciplina Java Advanced: modelagem e relacionamentos, fluxo do JWT, Spring AI, parte distribuída (RabbitMQ) e o app Mobile consumindo esta API.

## Vídeo pitch

Link público (YouTube): **https://youtu.be/5QRUoqrZJ-A**

Demonstra a API Java (Swagger): JWT, CRUD, Spring AI, RabbitMQ/alertas, papéis e integração com o fluxo do cidadão.

## Uso de IA generativa (declaração do time)

A IA generativa foi usada **apenas como apoio de estudo** (dúvidas, organização de ideias e revisão de conceitos).  
Não substitui o aprendizado nem a responsabilidade do grupo: o código entregue foi desenvolvido, testado e validado pelos integrantes.

## Entrega Java (checklist)

- [x] README com como rodar, usuários, endpoints, opção 5A (RabbitMQ)
- [x] Spring AI no fluxo da solução
- [x] API consumida pelo Mobile (link acima)
- [x] Link do vídeo pitch ≤ 5 min → https://youtu.be/5QRUoqrZJ-A
- [x] Vídeo da apresentação da Global Solution → https://youtu.be/osdBjXJQBMQ
- [ ] Link do deploy Azure da **API Java** (opcional — “se houver”; o DevOps publicou a API .NET de doações)

## Organização do repositório

- Código e migrations versionados em commits pequenos por tema  
- Evidências / PDFs → pasta `docs/`  
- Segredos → só variáveis de ambiente
