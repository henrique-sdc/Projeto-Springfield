# Projeto Springfield API - Plataforma de Serviços ao Cidadão

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-green.svg) <!-- Verifique a versão exata -->
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Spring State Machine](https://img.shields.io/badge/Spring-State%20Machine-blueviolet.svg)
![Kafka](https://img.shields.io/badge/Apache-Kafka-blue.svg)
![Actuator/Prometheus](https://img.shields.io/badge/Micrometer-Prometheus-orange.svg)
![OpenAPI](https://img.shields.io/badge/Docs-OpenAPI%203-informational.svg)
![Lombok](https://img.shields.io/badge/Lombok-Usado-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-Gestor-red.svg)

## 📌 Visão Geral

Este é o projeto principal da plataforma de serviços da cidade de Springfield. Ele fornece uma API RESTful para:

-   Gerenciamento de dados de **Cidadãos** (CRUD).
-   Gerenciamento de **Usuários** com funcionalidades de autenticação e segurança (cadastro, login, troca de senha, bloqueio, expiração).
-   Controle do ciclo de vida de **Solicitações** de serviço utilizando **Spring State Machine**, com persistência de histórico.
-   Exposição de **Métricas** (ex: total de usuários) para monitoramento via **Prometheus/Actuator**.
-   Sistema de **Comunicados** da prefeitura para cidadãos utilizando **Apache Kafka**.

> **Nota:** Inicialmente planejado para usar Azure SQL Server, o projeto foi implementado com MySQL local.

> **Microsserviço de IPTU:** As funcionalidades relacionadas ao IPTU (geração, consulta, pagamento) são gerenciadas por um microsserviço separado. Para detalhes e execução, consulte o repositório [Microservice-IPTU](https://github.com/henrique-sdc/Microservice-IPTU).

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.x** (Verifique a versão no `pom.xml`)
- **Spring Data JPA**
- **Spring Web**
- **Spring State Machine Core**
- **Spring Kafka**
- **Spring Boot Actuator**
- **Micrometer Prometheus Registry**
- **SpringDoc OpenAPI (Swagger UI)**
- **MySQL 8.0**
- **Lombok**
- **Maven**
- **Jakarta Validation**

## 📋 Pré-requisitos

Antes de executar o projeto, instale/tenha:

- **[JDK 17](https://www.oracle.com/java/technologies/downloads/)**
- **[Maven](https://maven.apache.org/download.cgi)**
- **[MySQL Server e Workbench](https://www.mysql.com/products/workbench/)** (ou outro cliente SQL)
- **[Apache Kafka e Zookeeper](https://kafka.apache.org/downloads)** (Recomendado usar Docker, veja o `docker-compose.yml` neste repositório)
- **Uma IDE** (IntelliJ IDEA, Eclipse, VS Code)
- **[Postman](https://www.postman.com/downloads/) (opcional)** para testes manuais de API

## 📂 Estrutura do Projeto (`springfield-api`)

```
Projeto-Springfield/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/springfield/springfield_api/
│   │   │   │   ├── config/           # Configs (Kafka)
│   │   │   │   ├── controller/       # Endpoints REST (Cidadao, Usuario, Solicitacao, Comunicado)
│   │   │   │   ├── kafka/            # Componentes Kafka (Producer, Consumer)
│   │   │   │   ├── model/            # Entidades JPA e DTOs (Cidadao, Usuario, SolicitacaoHistorico, ComunicadoDTO)
│   │   │   │   ├── repository/       # Repositórios JPA
│   │   │   │   ├── service/          # Regras de negócio (Cidadao, Usuario, Solicitacao)
│   │   │   │   ├── statemachine/     # Configuração do Spring State Machine e Actions
│   │   │   │   └── SpringfieldApiApplication.java
│   │   │   ├── resources/
│   │   │   │   └── application.properties  # Configuração da aplicação
│   │   └── test/                     # Testes unitários/integração
│   ├── pom.xml                     # Configuração do Maven
│   ├── docker-compose.yml          # Para rodar Kafka e Zookeeper
│   └── README.md                   # Este arquivo
```

## ⚙️ Configuração (`springfield-api`)

1.  **Clone este repositório:**
    ```bash
    git clone https://github.com/henrique-sdc/Projeto-Springfield.git
    ```

2.  **Crie e configure o Banco de Dados MySQL:**
    *   Crie o banco de dados `db_springfield`:
        ```sql
        CREATE DATABASE db_springfield;
         USE db_springfield;
         CREATE TABLE CAD_CIDADAO (
          ID INT PRIMARY KEY AUTO_INCREMENT,
          nome VARCHAR(255) NOT NULL,
          endereco VARCHAR(255) NOT NULL,
          bairro VARCHAR(255) NOT NULL
          );
         CREATE TABLE CAD_USUARIO_CIDADAO (
          ID INT PRIMARY KEY AUTO_INCREMENT,
          CIDADAO_ID INT UNIQUE,
          USERNAME VARCHAR(255) UNIQUE NOT NULL,
          SENHA VARCHAR(255) NOT NULL,
          BLOQUEADO BOOLEAN DEFAULT FALSE,
          TENTATIVAS_LOGIN INT DEFAULT 0,
          ULTIMO_LOGIN TIMESTAMP NULL,
          CONSTRAINT FK_CIDADAO FOREIGN KEY (CIDADAO_ID) REFERENCES CAD_CIDADAO(ID) ON DELETE CASCADE
          );
         INSERT INTO CAD_CIDADAO (ID, nome, endereco, bairro) VALUES
          (10001, 'João Silva', 'Rua das Flores, 123', 'Centro'),
          (10002, 'Maria Oliveira', 'Avenida Paulista, 456', 'Bela Vista'),
          (10003, 'Carlos Souza', 'Rua dos Pinheiros, 789', 'Jardins'),
          (10004, 'Ana Lima', 'Praça da Liberdade, 50', 'Liberdade'),
          (10005, 'Pedro Santos', 'Alameda Santos, 987', 'Jardim Paulista');
         INSERT INTO CAD_USUARIO_CIDADAO (CIDADAO_ID, USERNAME, SENHA, BLOQUEADO, TENTATIVAS_LOGIN, ULTIMO_LOGIN) VALUES
          (10001, 'joao.silva', 'senha123', FALSE, 0, NOW()),
          (10002, 'maria.oliveira', '123456', FALSE, 0, NOW()),
          (10003, 'carlos.souza', 'teste@123', FALSE, 0, NOW()),
          (10004, 'ana.lima', 'minhaSenha', FALSE, 0, NOW()),
          (10005, 'pedro.santos', 'segura123', FALSE, 0, NOW());
        ```

3.  **Configure Kafka e Zookeeper:**
    *   A maneira mais fácil é usando Docker. Um arquivo `docker-compose.yml` está incluído neste repositório. Na pasta raiz do projeto, execute:
        ```bash
        docker-compose up -d
        ```
    *   Isso iniciará Kafka na porta `9092` e Zookeeper na `2181`.

4.  **Configure `application.properties`:**
    *   Verifique e ajuste as configurações no arquivo `src/main/resources/application.properties`, especialmente:
        *   Credenciais do banco de dados MySQL (`username`, `password`).
        *   Endereço do Kafka: `spring.kafka.bootstrap-servers=localhost:9092`.
        *   Configurações do Actuator.
    ```properties
      spring.application.name=springfield
      logging.level.org.hibernate.SQL=DEBUG
      
      # Configuração do Banco de Dados SQL Workbench
      spring.datasource.url=jdbc:mysql://localhost:3306/db_springfield?useSSL=false&serverTimezone=UTC
      spring.datasource.username=root # SEU_USUARIO_MYSQL
      spring.datasource.password=root # SUA_SENHA_MYSQL
      spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
      
      # Configuração do Hibernate para SQL Server
      spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      
      # Actuator & Prometheus Configuração
      management.endpoints.web.exposure.include=prometheus,health,info
      management.endpoint.prometheus.enabled=true
      management.metrics.tags.application=${spring.application.name}
      management.metrics.tags.environment=development
      
      # === KAFKA CONFIGURATION ===
      spring.kafka.bootstrap-servers=localhost:9092
      
      spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
      spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
      
      spring.kafka.consumer.group-id=springfield-comunicados-group
      spring.kafka.consumer.auto-offset-reset=earliest
      spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
      spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
      spring.kafka.consumer.properties.spring.json.trusted.packages=*
      spring.kafka.consumer.properties.spring.json.use.type.headers=false
      
      # Nome do tópico
      springfield.kafka.topic.comunicados=prefeitura-avisos
    ```

5.  **Importe o projeto `springfield` na sua IDE.**

## ▶️ Executando a Plataforma

1.  **Kafka e Zookeeper:** Certifique-se de que estão rodando (Passo 3 da Configuração).
2.  **Este Serviço (`springfield-api`):**
    *   Navegue até a pasta do projeto (ex: `Projeto-Springfield/`).
    *   Execute pela IDE (classe `SpringfieldApiApplication`) ou via Maven:
        ```bash
        mvn spring-boot:run
        ```
    *   Este serviço estará disponível na porta `8080`.
3.  **(Opcional) Serviço de IPTU:**
    *   Clone e configure o repositório `Microservice-IPTU`.
    *   Execute-o (normalmente na porta `8081`). Ele se comunicará com este serviço na porta `8080`.

## 🚀 Testando a API (Porta 8080)

Uma coleção Postman (`Projeto Springfield - Postman Collection.json`) está disponível neste repositório com exemplos de requisições para todas as funcionalidades.

### Endpoints Principais:

-   **Cidadãos:** `/cidadaos` (GET, POST), `/cidadaos/{id}` (GET, PUT, DELETE)
-   **Usuários:** `/usuarios/cadastro` (POST), `/usuarios/login` (POST), `/usuarios/{id}/trocar-senha` (PUT), `/usuarios/{id}/desbloquear` (PUT)
-   **Solicitações (State Machine):**
    -   `POST /solicitacoes` (Body: `{ "cidadaoId": "...", "descricao": "..." }`)
    -   `POST /solicitacoes/{demandaId}/eventos` (Body: `{ "evento": "ANALISAR|CONCLUIR" }`)
    -   `GET /solicitacoes/cidadao/{cidadaoId}`
-   **Comunicados Prefeitura (Kafka):**
    -   `POST /comunicados/publicar` (Body: `{ "titulo": "...", "mensagem": "...", "remetente": "..." }`)
    -   `GET /comunicados/visualizar`

## 📜 Documentação e Métricas

-   **Documentação OpenAPI (Swagger):** `http://localhost:8080/swagger-ui.html`
-   **Métricas Prometheus:** `http://localhost:8080/actuator/prometheus` (Procure por `springfield_api_registered_users_count`)

## 📌 Melhorias Futuras (Sugestões)

-   Implementar autenticação e autorização robustas (ex: JWT, OAuth2).
-   Adicionar paginação e filtros nas listagens de APIs.
-   Expandir a suíte de testes unitários e de integração.
-   Melhorar o tratamento de erros e resiliência (ex: Retry, Circuit Breaker para chamadas Feign).
-   Para Kafka, considerar persistência de mensagens e tratamento de DLQ (Dead Letter Queue).
