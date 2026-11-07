# FloodShield Java API

Backend Spring Boot da CitizenTech **FloodShield** — Global Solution FIAP 2026/2.

Repositório: https://github.com/FloodShield-2026/FloodShield_Java

## Equipe

| Nome | RM |
| --- | --- |
| Bruno Vinicius Barbosa | 566366 |
| Guilherme de Andrade Martini | 566087 |
| Nathan Gonçalves Pereira Mendes | 564666 |
| Raphael Gomes Mancera | 562279 |

## Stack

- Java 21 + Spring Boot 3.4
- Spring Data JPA + H2 (dev) / PostgreSQL (prod)
- Spring Security + JWT (`CIDADAO`, `GESTOR`)
- Bean Validation + `@RestControllerAdvice`
- Spring AMQP / RabbitMQ (evento `NivelCritico`)
- Spring AI (`ChatModel` + RAG da cartilha local)
- springdoc OpenAPI / Swagger UI
- Paginação nos CRUDs

## Como rodar

```bash
# opcional — sobe RabbitMQ
docker compose up -d

mvn spring-boot:run
```

- API: http://localhost:8080  
- Swagger: http://localhost:8080/swagger-ui.html  
- Health: http://localhost:8080/actuator/health  

Para ativar o consumer RabbitMQ:

```bash
RABBITMQ_LISTENER_ENABLED=true mvn spring-boot:run
```

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
| POST | `/ai/plano-acao` | Plano de ação (Spring AI + RAG) |
| GET | `/alertas` | Alertas gerados |

## Arquitetura distribuída (requisito 5A)

Quando uma leitura passa do limite do ponto, a API:

1. Publica `NivelCriticoEvent` no exchange RabbitMQ  
2. O consumer grava um `Alerta`  
3. Se o broker estiver offline, o serviço ainda persiste o alerta localmente  

## Spring AI

O fluxo de plano de ação usa `ChatClient` / `ChatModel` com recuperação de trechos de `cartilha/protocolo-enchente.txt` (RAG básico). Não é chat genérico solto: só roda no endpoint `/ai/plano-acao` ligado à ocorrência/nível.

## Uso de IA generativa (declaração GS)

Ferramentas de IA apoiaram scaffolding e revisão. O código foi revisado pelo grupo. A feature de produto usa Spring AI conforme acima.

## Próximos passos

- Perfil Azure SQL + deploy App Service (DevOps)
- Ligar o app Mobile em `EXPO_PUBLIC_JAVA_API_URL`
- Vídeo pitch ≤ 5 min
