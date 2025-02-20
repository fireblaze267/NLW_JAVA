# Documentação da API - NLW Connect

API desenvolvida durante o NLW Connect para gerenciamento de eventos e inscrições.

## Visão Geral

API construída com Spring Boot que permite:

- Criação e consulta de eventos
- Controle de inscrições em eventos
- Geração de rankings de participantes

### Endpoints

### Eventos

#### 1. Listar Eventos

**URL:** `/events`  
**Método:** `GET`  
**Descrição:** Retorna todos os eventos ou filtra por prettyName  
**Parâmetros:**

- `prettyName` (opcional): Filtra eventos pelo identificador único

**URL:** `/events`  
**Método:** `POST`  
**Descrição:** Cria um novo evento.

**Corpo da Requisição:**

```json
{
        "eventId": 5,
        "title": "CodeCraft Summer 2025",
        "prettyName": "codecraft-summer-2025",
        "location": "online",
        "price": 0.0,
        "endDate": "2025-03-18",
        "endTime": "21:00:00",
        "starDate": null,
        "starTime": null
}
```

### Subscription

**URL:** `/subscription/{prettyName}`  
**Método:** `POST`  
**Descrição:** Cria usuário (se não existir) e inscreve no evento.
**Parâmetros:**

- prettyName (obrigatório): Identificador do evento

**Corpo da Requisição:**

```json
{
       "name":"teste",
       "email":"teste@teste.com",
}
```

**URL:** `/subscription/{prettyName}/{userId}`  
**Método:** `POST`  
**Descrição:** Cria inscrição com indicação de usuário existente.
**Parâmetros:**

- prettyName (obrigatório): Identificador do evento
- userId (obrigatório): ID do usuário que está indicando

**Corpo da Requisição:**

```json
{
       "name":"teste",
       "email":"teste@teste.com",
}
```

**URL:** `/subscription/{prettyName}/ranking`
**Método:** `GET`
**Descrição:** Retorna ranking completo do evento.

**URL:** `/subscription/{prettyName}/{userId}`  
**Método:** `GET`  
**Descrição:** Retorna posição específica no ranking.
