# 🎯 Event Manager API

API REST para gerenciamento de eventos, cupons e endereços, com integração em nuvem para armazenamento de imagens.

---

## 🚀 Tecnologias utilizadas

* Java + Spring Boot
* Spring Data JPA
* PostgreSQL
* Flyway (controle de migrations)
* AWS (S3, EC2 e RDS - PostgreSQL)
* H2 Database (ambiente de desenvolvimento)

---

## 📌 Funcionalidades

* ✅ Cadastro de eventos
* ✅ Upload de imagens para a nuvem (AWS S3)
* ✅ Listagem de eventos futuros
* ✅ Filtro de eventos por:

    * título
    * cidade
    * estado (UF)
    * intervalo de datas
* ✅ Gerenciamento de cupons
* ✅ Relacionamento entre evento e endereço

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

* **Controller** → Responsável pelos endpoints da API
* **Service** → Contém as regras de negócio
* **Repository** → Acesso aos dados e queries customizadas
* **DTOs** → Transferência de dados entre camadas
* **Domain** → Entidades e modelos de domínio da aplicação

---

## 🗄️ Banco de dados

O projeto utiliza:

* PostgreSQL em produção (AWS RDS)
* H2 para testes locais
* Flyway para versionamento do banco

---

## ⚙️ Configuração do ambiente

As credenciais não são versionadas por segurança.

Crie um arquivo `application.yml` baseado no exemplo:

```yaml id="3gp9a7"
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

aws:
  region: ${AWS_REGION}
  bucket:
    name: ${AWS_BUCKET_NAME}
```

---

## 🔐 Variáveis de ambiente

Configure as seguintes variáveis:

* `DB_URL`
* `DB_USERNAME`
* `DB_PASSWORD`
* `AWS_REGION`
* `AWS_BUCKET_NAME`

---

## ▶️ Como executar o projeto

1. Clone o repositório:

```bash id="9dt37c"
git clone https://github.com/Joao-Pedro-SA/EventManager_API.git
```

2. Configure as variáveis de ambiente

3. Execute o projeto:

```bash id="q4uvmt"
./mvnw spring-boot:run
```

---

## 📡 Exemplos de endpoints

### 🔍 Buscar eventos com paginação

```
GET /api/event?page=&size=
```

### 🔎 Filtrar eventos

```
GET /api/event/filter?title=&city=&uf=&startDate=&endDate=
```

---

## 📷 Upload de imagens

As imagens dos eventos são armazenadas no AWS S3, garantindo escalabilidade e disponibilidade.

---

## 📚 Créditos

Este projeto foi desenvolvido com base em conteúdos e aulas da criadora de conteúdo Fernanda Kipper.

O objetivo deste repositório é estudo, prática e aprofundamento dos conceitos apresentados, com adaptações e implementações próprias.

---

## 🧠 Aprendizados

Este projeto foi desenvolvido com foco em:

* Construção de APIs REST
* Integração com serviços em nuvem (AWS)
* Boas práticas de arquitetura em camadas
* Controle de versão de banco de dados com Flyway


---

## ☁️ Uso da AWS e Otimização de Custos

Este projeto foi inicialmente implantado utilizando serviços da Amazon Web Services, incluindo:

* EC2 para execução da aplicação 
* RDS (PostgreSQL) para banco de dados 
* S3 para armazenamento de imagens

Atualmente, os recursos de EC2 e RDS foram pausados/removidos temporariamente, com a criação de snapshots do banco de dados, com o objetivo de:

* Otimizar o uso de créditos da AWS.
* Permitir o estudo de outros serviços da plataforma. 
* Reduzir custos desnecessários de recursos em execução contínua.

A aplicação pode ser executada localmente utilizando:

Banco H2 (já configurado) ou PostgreSQL local.

Os recursos podem ser restaurados a qualquer momento a partir dos snapshots.

---

## 👨‍💻 Autor

Desenvolvido por João Pedro
