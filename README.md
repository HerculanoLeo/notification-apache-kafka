# Notification System with Apache Kafka

## 🎯 Objetivo do Projeto
Esse é um projeto de microsserviços simplificado com o objetivo primário de implementar o gerenciamento de eventos e filas de processos de notificação. A arquitetura utiliza o **Apache Kafka** para a distribuição confiável e assíncrona dos eventos e o **Redis** para garantir o controle de idempotência das notificações (assegurando que uma mesma notificação não seja reprocessada ou reenviada duplicadamente).

## 🏗 Estrutura do Projeto
O sistema foi desenvolvido sob uma arquitetura de microsserviços e é composto, em essência, pelos seguintes serviços principais:

- **Notification (`/notification`)**: Ponto de entrada das notificações. Funciona reativamente recebendo os pedidos e publicando esses eventos de notificação nos tópicos do Kafka.
- **Processor (`/processor`)**: Serviço encarregado de rodar processos em background e interagir com fontes externas de dados. Processa as regras de negócio, agenda tarefas, se comunica via APIs HTTP externas e produz novas mensagens para a fila de envio.
- **Email Sender (`/email-sender`)**: Aplicação consumidora das mensagens do Kafka. Responsável por efetivar o disparo de e-mails, consultando o Redis antes do envio para assegurar a idempotência do evento.
- **Liquibase (`/liquibase`)**: Controles padronizados e versionamento para migrações do banco de dados relacional.
- **Docker Compose**: Arquivos para infraestrutura local contendo Kafka, instâncias do PostgreSQL e do Valkey/Redis para facilitar os testes da solução.

## 🚀 Funcionalidades Implementadas
- Recepção e enfileiramento reativo de pedidos de notificação.
- Comunicação assíncrona e orientada a eventos usando múltiplos tópicos do Apache Kafka.
- Consumo resiliente de mensagens distribuídas no serviço `email-sender`.
- Controle rigoroso de **idempotência** e unicidade de envios através de armazenamento em cache via Redis.
- Agendamento de tarefas em background com Quartz no serviço `processor`.
- Persistência híbrida baseada nas necessidades do serviço, oferecendo comunicação tanto de forma reativa (`R2DBC`) quanto bloqueante tradicional (`JPA / Hibernate`).
- Containerização e orquestração de ambiente completo usando Docker Compose prontas para uso local.

## 🛠 Bibliotecas e Tecnologias Usadas

### Stack Principal e Infraestrutura
- **Linguagens**: Kotlin & Java 21 LTS
- **Message Broker**: Apache Kafka
- **Cache/Idempotência**: Redis / Valkey
- **Banco de Dados**: PostgreSQL
- **Build Tool**: Gradle (Kotlin DSL)
- **Containerização**: Docker e Docker Compose

### Ecossistema Spring Boot 3.3.x

Pelo fato do sistema estar componentizado, os serviços implementam bibliotecas específicas de acordo com suas responsabilidades:

* **Microsserviço `Notification`**:
  * **Spring WebFlux**: Programação baseada em reatividade (Non-blocking I/O).
  * **Spring Data R2DBC**: Acesso reativo e de alto desempenho a dados relacionais (Postgres).
  * **Spring Kafka**: Produtor assíncrono de eventos no Kafka.
  * **FreeMarker**: Processamento de templates.

* **Microsserviço `Processor`**:
  * **Spring Web (MVC)**: Comunicação e APIs tradicionais.
  * **Spring Data JPA / Hibernate**: Mapeamento objeto-relacional (ORM) e consultas bloqueantes.
  * **Spring Quartz**: Controle robusto para o agendamento de Jobs (`Scheduled Tasks`).
  * **Spring Cloud OpenFeign**: Clientes HTTP declarativos para comunicação segura externa.
  * **Liquibase Core**: Gestão e controle de versão do DB.

* **Microsserviço `Email Sender`**:
  * **Spring WebFlux**: Framework base do serviço.
  * **Spring Mail**: Serviço que orquestra a abstração e envio SMTP.
  * **Spring Kafka**: Listeners e consumidores dos tópicos de notificação.
  * **Spring Data Redis Reactive**: Armazenamento ágil de chaves para evitar duplicação no envio das mensagens.

## 🐳 Como Executar Localmente
O projeto já conta com o ambiente pré-configurado para que toda a infraestrutura e microsserviços rodem localmente:

1. Renomeie ou copie o arquivo `.env.exemple` para `.env` ajustando as credenciais e as configurações de ambiente caso deseje.
   ```bash
   cp .env.exemple .env
   ```
2. Renomeie o construtor do liquibase também `liquibase.properties.exemple` para `liquibase.properties`:
   ```bash
   cp liquibase/liquibase.properties.exemple liquibase/liquibase.properties
   ```
3. Suba o ecossistema com os containers em *background*:
   ```bash
   docker-compose up -d
   ```
> **Aviso**: Certifique-se de ter suas portas (`9000`, `9001`, Kafka, PostgreSQL) livres na máquina local e verifique os logs via `docker-compose logs -f` para assegurar que os containers estão saudáveis.

---
*Esse repositório serve a um propósito de portfólio demonstrativo, destacando a combinação, controle de idempotência e desafios arquiteturais envolvendo Kafka, Redis, mensageria distribuída e o ecossistema reativo/moderno fornecido por Spring Boot com Kotlin.*
