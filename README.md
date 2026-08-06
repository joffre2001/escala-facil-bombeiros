# EscalaFácil Bombeiros

MVP do sistema web para elaboração automática de escalas mensais de plantões de 24 horas.

## Primeiro marco implementado

- Java 21 e Spring Boot;
- API REST de bombeiros;
- validação de dados e erros padronizados;
- PostgreSQL com Docker Compose;
- migração de banco com Flyway;
- documentação Swagger/OpenAPI.

## Como executar

Requisitos: Java 21, Maven e Docker Desktop.

```bash
docker compose up -d
mvn spring-boot:run
```

Abra o Swagger em: `http://localhost:8080/api/swagger-ui.html`

## Primeiro cadastro pelo Swagger ou Postman

`POST http://localhost:8080/api/firefighters`

```json
{
  "nomeCompleto": "João da Silva",
  "matricula": "BM-001",
  "email": "joao@bombeiros.org",
  "telefone": "(49) 99999-0000",
  "cargo": "Bombeiro",
  "equipe": "Equipe A",
  "dataAdmissao": "2024-01-15"
}
```

## Próximos marcos

1. Usuários, perfis e autenticação JWT.
2. Equipes, férias e afastamentos.
3. Períodos e disponibilidades mensais.
4. Algoritmo de geração e validação da escala.
5. Aprovação, publicação e interface web responsiva.
