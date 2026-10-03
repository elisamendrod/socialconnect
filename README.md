# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot · Spring Data JPA · Flyway · PostgreSQL

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- PostgreSQL (local na porta 5432 ou via Docker)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### 1. Iniciar o Banco de Dados (PostgreSQL)

Caso use Docker:
```bash
docker compose up -d
```

Ou certifique-se de que o seu serviço local do PostgreSQL está rodando com uma base de dados chamada `socialconnect`.
> As credenciais padrão configuradas em `application.properties` são:
> - **Host**: `localhost:5432`
> - **Database**: `socialconnect`
> - **User**: `postgres`
> - **Password**: `postgres`
>
> Você também pode sobrescrevê-las usando as variáveis de ambiente `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD`.

### 2. Compilar e Rodar a Aplicação

```bash
# Compile o projeto
./mvnw clean compile

# Rode a aplicação (as migrações do Flyway executarão automaticamente)
./mvnw spring-boot:run
```

A documentação Swagger estará disponível em:
`http://localhost:8080/swagger-ui.html`
## Modulo de Produtos (A1)

No Windows:
```powershell
mvn clean compile
mvn spring-boot:run
```

Para executar sem PostgreSQL/Docker (banco H2 temporario):
```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Swagger: http://localhost:8080/swagger-ui.html
OpenAPI: http://localhost:8080/api-docs

| Metodo | Endpoint | Resultado |
| --- | --- | --- |
| GET | /api/v1/produtos?nome=arroz&categoria=ALIMENTO&page=0&size=10&sort=nome,asc | Lista paginada |
| GET | /api/v1/produtos/{id_produto} | Consulta |
| POST | /api/v1/produtos | 201 e Location |
| PUT | /api/v1/produtos/{id_produto} | Atualizacao completa |
| DELETE | /api/v1/produtos/{id_produto} | 204 |

Exemplo de JSON:
```json
{"nome":"Arroz 5kg","categoria":"ALIMENTO","estoqueAtual":3,"estoqueMinimo":10,"unidadeMedida":"unidade"}
```

Nome repetido retorna 409; estoque negativo retorna 422; dados invalidos
retornam 400; produto inexistente retorna 404. Erros usam Problem Details.
A resposta inclui estoqueBaixo quando estoqueAtual < estoqueMinimo.


Esta pasta recebida nao possui metadados Git; branch/push dependem do
repositorio pessoal do aluno.

## Verificacao realizada em 02/10/2026

- mvn clean compile: BUILD SUCCESS.
- mvn test: BUILD SUCCESS, 20 testes aprovados, zero falhas e zero erros.
- 8 testes dependentes de Docker foram ignorados porque o Docker estava parado.
- Perfil local validado com Flyway, CRUD, filtros, validacoes e Swagger.
- Para executar os testes PostgreSQL 17/Testcontainers, inicie o Docker e rode mvn test.
- O perfil local usa H2 em memoria: os dados desaparecem ao encerrar a aplicacao.
- As credenciais PostgreSQL podem ser definidas por DB_HOST, DB_PORT, DB_NAME,
  DB_USERNAME e DB_PASSWORD; o padrao segue docker-compose.yml.
