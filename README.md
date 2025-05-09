# Projeto Springfield API - Gerenciamento de Cidadãos

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Lombok](https://img.shields.io/badge/Lombok-Usado-brightgreen.svg)
![Spring State Machine](https://img.shields.io/badge/Spring-State%20Machine-blueviolet.svg)
![Actuator/Prometheus](https://img.shields.io/badge/Micrometer-Prometheus-orange.svg)
![OpenAPI](https://img.shields.io/badge/Docs-OpenAPI%203-informational.svg)

## 📌 Visão Geral

Este projeto é a API RESTful principal para gerenciar o cadastro de Cidadãos, controle de acesso de Usuários (login, bloqueio, etc.) e o ciclo de vida de Solicitações de serviço (usando Spring State Machine) para a cidade fictícia de Springfield. Ele também expõe métricas para Prometheus.

> **Nota:** Inicialmente planejado para usar Azure SQL Server, mas foi implementado com MySQL local.

> **Microsserviço de IPTU:** Para funcionalidades relacionadas ao IPTU (geração, consulta, pagamento), consulte o projeto `Microservice-IPTU` neste [link](https://github.com/henrique-sdc/Microservice-IPTU), que opera como um microsserviço separado.

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.3**
- **Spring Data JPA**
- **MySQL 8.0**
- **Lombok**
- **Maven**
- **Postman** (para testes de API)

## 📋 Pré-requisitos

Antes de executar o projeto, instale:

- **[JDK 17](https://www.oracle.com/java/technologies/downloads/)**
- **[Maven](https://maven.apache.org/download.cgi)**
- **[MySQL Server e Workbench](https://www.mysql.com/products/workbench/)**
- **Uma IDE** (IntelliJ IDEA, Eclipse, VS Code)
- **[Postman](https://www.postman.com/downloads/) (opcional)**

## 📂 Estrutura do Projeto

```
springfield-api/
├── src/
│   ├── main/
│   │   ├── java/com/springfield/springfield_api/
│   │   │   ├── controller/       # Endpoints REST
│   │   │   ├── model/            # Entidades
│   │   │   ├── repository/       # Repositórios
│   │   │   ├── service/          # Regras de negócio
│   │   │   └── SpringfieldApiApplication.java
│   │   ├── resources/
│   │   │   └── application.properties  # Configuração
│   └── test/  # Testes
├── pom.xml       # Configuração do Maven
└── README.md     # Este arquivo
```

## ⚙️ Configuração

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/henrique-sdc/Projeto-Springfield.git
   cd springfield-api
   ```

2. **Crie o banco de dados:**
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

3. **Configure `application.properties`:**
   ```properties
    spring.application.name=springfield
    logging.level.org.hibernate.SQL=DEBUG
    spring.datasource.url=jdbc:mysql://localhost:3306/db_springfield?useSSL=false&serverTimezone=UTC
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
   ```

4. **Importe o projeto na sua IDE.**

## ▶️ Executando a Plataforma Completa (API + IPTU)

Para utilizar todas as funcionalidades, incluindo o serviço de IPTU, siga estes passos:

1.  **Inicie este serviço (`springfield-api`):**
    *   Navegue até a pasta `springfield-api`.
    *   Execute pela IDE (classe `SpringfieldApiApplication`) ou via Maven:
        ```bash
        mvn spring-boot:run
        ```
    *   Este serviço estará disponível na porta `8080`.

2.  **Inicie o serviço `iptu-service`:**
    *   Navegue até a pasta `iptu-service`.
    *   Certifique-se que ele está configurado (veja o `README.md` dentro de `iptu-service`).
    *   Execute pela IDE (classe `IptuServiceApplication`) ou via Maven:
        ```bash
        mvn spring-boot:run
        ```
    *   Este serviço estará disponível na porta `8081` e se comunicará com o `springfield-api` na porta `8080`.

## 🚀 Testando a API (Postman)

Use o Postman ou similar para interagir com a API. Uma coleção completa com mais cenários pode estar disponível no repositório.

### Cidadãos

- **Listar todos os cidadãos**
  ```http
  GET http://localhost:8080/cidadaos
  ```

- **Buscar um cidadão por ID**
  ```http
  GET http://localhost:8080/cidadaos/{id}
  ```

- **Cadastrar um novo cidadão**
  ```http
  POST http://localhost:8080/cidadaos
  Content-Type: application/json

  {
    "nome": "Lucas Ferreira",
    "endereco": "Rua Nova, 123",
    "bairro": "Centro"
  }
  ```

- **Atualizar um cidadão**
  ```http
  PUT http://localhost:8080/cidadaos/{id}
  Content-Type: application/json

  {
  "nome": "Atualizado",
  "endereco": "Rua das Palmeiras, 999",
  "bairro": "Vila Nova"
  }
  ```

- **Deletar um cidadão**
  ```http
  DELETE http://localhost:8080/cidadaos/{id}
  ```

### Usuários

- **Cadastrar um usuário**
  ```http
  POST http://localhost:8080/usuarios/cadastro
  Content-Type: application/json

  {
  "idCidadao": 10009,
  "username": "cleber.silva",
  "senha": "senha123"
  }
  ```

- **Login de usuário**
  ```http
  POST http://localhost:8080/usuarios/login
  Content-Type: application/json

  {
  "username": "joao.silva",
  "senha": "senha123"
  }
  ```

- **Trocar senha do usuário**
  ```http
  PUT http://localhost:8080/usuarios/{id}/trocar-senha
  Content-Type: application/json

  {
  "novaSenha": "novaSenha456"
  }
  ```

- **Desbloquear usuário**
  ```http
  PUT http://localhost:8080/usuarios/{id}/desbloquear
  ```

## 📜 Documentação e Métricas

-   **Documentação OpenAPI (Swagger):** `http://localhost:8080/swagger-ui.html`
-   **Métricas Prometheus:** `http://localhost:8080/actuator/prometheus`

## 📌 Melhorias Futuras

- Implementar autenticação JWT
- Adicionar paginação na listagem
- Criar testes unitários
- Adicionar documentação Swagger/OpenAPI

