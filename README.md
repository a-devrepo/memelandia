# Memelandia

Sistema distribuído baseado em microserviços para o gerenciamento de memes e usuários, focado em alta disponibilidade, persistência relacional e observabilidade.

## Arquitetura e Componentes

A solução é dividida em serviços especializados que garantem a separação de responsabilidades:

* **Discovery Server:** Service registry utilizando Netflix Eureka para descoberta dinâmica de instâncias.
* **API Gateway:** Ponto de entrada único (Porta 8081), responsável pelo roteamento e filtros de log.
* **Meme Service:** Gerencia o catálogo de memes, categorias e metadados de mídia.
* **User Service:** Responsável pela gestão de usuários e regras de validação.

## Pilha Tecnológica

* **Core:** Java 17, Spring Boot 3, Spring Cloud.
* **Persistência:** PostgreSQL (Instância dedicada para cada microserviço de domínio).
* **Mensageria:** RabbitMQ (Comunicação assíncrona para eventos de sistema).
* **Observabilidade:** Spring Actuator, Micrometer, Zipkin (Tracing distribuído).
* **Documentação:** OpenAPI 3 / Swagger.
* **Containerização:** Docker e Docker Compose.

## Persistência de Dados

Cada microserviço de domínio possui sua própria base de dados **PostgreSQL**, garantindo o isolamento de dados (Database-per-Service):

* **db-memes:** Armazena entidades de memes e categorias.
* **db-users:** Armazena perfis e credenciais de usuários.

## Observabilidade e Tracing

O sistema implementa rastreamento ponta a ponta para monitorar o ciclo de vida das requisições:

*  **TraceID:** Gerado no API Gateway e propagado via cabeçalhos HTTP e mensagens RabbitMQ.
*  **Zipkin:** Interface para visualização de latência e dependências entre serviços.
*  **Logs:** Configurados para exibir `traceId` e `spanId`, facilitando a correlação de eventos no console.

## Infraestrutura (Docker Compose)

O arquivo `docker-compose.yml` na raiz do projeto orquestra os seguintes serviços de suporte:

```yaml
services:
  discovery-server:
    build: ./discoveryserverapp
    container_name: discovery-server
    ports:
      - "8761:8761"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - meme-network

  user-db:
    image: postgres:17
    container_name: user-db
    environment:
      POSTGRES_DB: user_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    ports:
      - "5433:5432"
    networks:
      - meme-network

  meme-db:
    image: postgres:17
    container_name: meme-db
    environment:
      POSTGRES_DB: meme_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    ports:
      - "5434:5432"
    networks:
      - meme-network

  rabbitmq:
    image: rabbitmq:4.0-management
    container_name: meme-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    networks:
      - meme-network
      
  api-gateway:
    build: ./apigateway
    container_name: api-gateway
    restart: always
    ports:
      - "8081:8081"
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
      - LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CLOUD_GATEWAY=TRACE
    depends_on:
      discovery-server:
        condition: service_healthy
    networks:
      - meme-network
  
  zipkin:
    image: openzipkin/zipkin
    container_name: meme-zipkin
    ports:
      - "9411:9411"
    networks:
      - meme-network

  user-service:
    build: ./userservice
    container_name: user-service
    ports:
      - "8082:8082"
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
      - SPRING_DATASOURCE_URL=jdbc:postgresql://user-db:5432/user_db
      - MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://meme-zipkin:9411/api/v2/spans
    depends_on:
      user-db:
        condition: service_started
      discovery-server:
        condition: service_healthy
    networks:
      - meme-network
  
  meme-service:
    build: ./memeservice
    container_name: meme-service
    ports:
      - "8083:8083"
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
      - SPRING_DATASOURCE_URL=jdbc:postgresql://meme-db:5432/meme_db
      - MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://meme-zipkin:9411/api/v2/spans
    depends_on:
      user-db:
        condition: service_started
      discovery-server:
        condition: service_healthy
    networks:
      - meme-network

networks:
  meme-network:
    driver: bridge
 ```
## Como Executar
Pré-requisitos

    Java 21+

    Maven 3.8+

    Docker Desktop / Engine
    
    
## Inicie os containers de infraestrutura:

docker-compose up -d

## Compile e execute o Discovery Server primeiro para permitir o registro dos demais.

    Execute o API Gateway.

    Execute o Meme Service e o User Service.

## Documentação da API

Após a inicialização, a documentação interativa estará disponível via Gateway:

    Meme Service API: http://localhost:8081/meme-service/v3/api-docs

    User Service API: http://localhost:8082/user-service/v3/api-docs
