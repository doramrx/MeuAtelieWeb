# MeuAtelieWeb

## :pushpin: Conteúdo

* [Sobre](#Sobre)
* [Tecnologias](#Tecnologias)
* [Screenshots](#Screenshots)
    * [Vídeo Demonstrativo](#Video)
    * [Prototipo](#Prototipo)
    * [Modelagem](#Modelagem)
* [Como executar o projeto](#Executando)
* [Endpoints](#Endpoints)

<a name="Sobre"></a>
## :dress: Sobre
O projeto **MeuAteliêWeb** foi desenvolvido com base no projeto [MeuAteliêApp](https://github.com/doramrx/MeuAtelieApp), que foi desenvolvido durante a disciplina de Projeto Integrador I e II do curso de Análise e Desenvolvimento de Sistemas (ADS) do Instituto Federal de Santa Catarina (IFSC).

A versão mobile do MeuAtelie busca entregar para donas de ateliês de roupa um aplicativo intuitivo e de fácil uso que permita gerenciar e automatizar processos realizados diáriamente, como realizar a criação de pedidos de ajuste de roupa e de roupa sob medida, salvar as medidas dos clientes em cada pedido, acompanhar a agenda de pedidos e consultar o histórico de pedidos.

A proposta dessa versão web é expandir o uso do sistema do dispositivo mobile para o desktop para permitir utilizar funcionalidades que são mais convenientes de serem utilizadas em um computador.

<a name="Tecnologias"></a>
## :zap: Tecnologias
### 🗄️ Backend 
* [Java](https://www.java.com/pt-BR/) (OpenJDK 17)
* [Spring Boot](https://spring.io/projects/spring-boot/)
* [Flyway](https://flywaydb.org/)
* [JUnit5](https://junit.org/junit5/)
* [Mockito](https://site.mockito.org/)
* [Lombok](https://projectlombok.org/)
* [PostgreSQL](https://www.postgresql.org/)
* [Docker](https://www.docker.com/)
* [Docker compose](https://docs.docker.com/compose/)
* [Google Cloud Platform (GCP)](https://cloud.google.com/) 

### 🖥️ Frontend
* [Typescript](https://www.typescriptlang.org/)
* [Angular](https://angular.io/) (Versão 17)
* [PrimeNG](https://primeng.org/)
* [RxJS](https://rxjs.dev/)
* [Figma](https://www.figma.com/)

<a name="Screenshots"></a>
## :camera: Screenshots

<a name="Video"></a>
### :movie_camera: Vídeo Demonstrativo

[Link Vídeo Demonstrativo](https://www.loom.com/share/13e3f316d5ba453583e0f6007dfa33bf?sid=071c543c-d3ee-4e59-8d9c-a38a7262c1cd)

<a name="Prototipo"></a>
### :desktop_computer: Prototipo

[Link do protótipo no Figma](https://www.figma.com/file/l0Q6gyK6yuIDq9pN06W836/MeuAtelieWeb?type=design&node-id=0%3A1&mode=design&t=P0ffH15TKVIp86d4-1)

![lista](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/67439126-e1e1-463b-af41-39e7bbb94bf6)
![filtrar](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/e6ef9ae6-13ae-41c4-a13f-65fdf87c023e)
![adicionar](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/08f8a73c-abbb-40d8-9947-1f19df3a9619)
![sucesso](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/2ce3f6e3-99c1-4b4c-a7b9-0804ed99077d)
![editar](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/cbf849ff-8d59-45a2-afa9-6a9b53281446)
![deletar](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/c3a27626-19eb-4f91-8fe4-74a42b6fc6aa)
![inativo](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/5ad6b8dd-338a-4b75-875e-fb8889def621)

<a name="Modelagem"></a>
### :game_die: Modelagem

![logicalModel](https://github.com/doramrx/MeuAtelieWeb/assets/87739902/a4857a97-16bb-4382-bdf2-891a320cbe01)

<a name="Executando"></a>
## 📋 Executando o projeto
Para executar o projeto, é necessário ter o docker e o docker compose instalados no seu ambiente de desenvolvimento. 

Para subir os projetos execute os seguintes passos:
```shell
$ git clone https://github.com/doramrx/MeuAtelieWeb
$ cd MeuAtelieWeb
$ docker compose up -d
```

Quando o docker terminar de subir todos os projetos, a API estará disponível para ser utilizada em `http://localhost:8080` e o frontend estará disponível no endereço `http://localhost:4200`

<a name="Endpoints"></a>
## :paperclip: Endpoints
<details>
  <summary>
    <strong>Cliente</strong>
  </summary>

#### Retorna todos os clientes

```http
  GET http://localhost:8080/customers
```

##### Parametros da rota
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome do cliente |
| `email` | `string` | *Opcional*. Email do cliente |
| `phone` | `string` | *Opcional*. Telefone do cliente |
| `isActive` | `boolean` | *Opcional*. Valor boleano que indica se o cliente está ativo ou não |

##### Exemplo de resposta:
```json
  {
    "totalPages": 1,
    "totalElements": 2,
    "content": [
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Maria",
            "email": "maria.aparecida@gmail.com",
            "phone": "47911111111",
            "isActive": true
        },
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Roberta da Silva",
            "email": "roberta.silva@gmail.com",
            "phone": "47911111111",
            "isActive": false
        }
    ]
}
```

#### Retorna um cliente

```http
  GET http://localhost:8080/customers/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do cliente |

##### Exemplo de requisição:
```http
  GET http://localhost:8080/customers/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Maria",
    "email": "maria.aparecida@gmail.com",
    "phone": "47911111111",
    "isActive": true
}
```

#### Cria um cliente

```http
  POST http://localhost:8080/customers
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | **Obrigatório**. Nome do cliente |
| `email` | `string` | **Obrigatório**. Email do cliente |
| `phone` | `string` | **Opcional**. Telefone do cliente |

##### Exemplo de criação de um cliente:
```json
{
    "name": "Maria",
    "phone": "47911111111",
    "email": "maria.aparecida@gmail.com"
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Maria",
    "email": "maria.aparecida@gmail.com",
    "phone": "47911111111",
    "isActive": true
}
```

#### Atualiza os dados de um cliente

```http
  PUT http://localhost:8080/customers/{id} 
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do cliente |

##### Exemplo de requisição:
```http
  PUT http://localhost:8080/customers/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome do cliente |
| `phone` | `string` | *Opcional*. Telefone do cliente |

##### Exemplo de requisição:
```json
{
    "name": "Maria Aparecida de Andrade",
    "phone": "47922222222"
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Maria Aparecida de Andrade",
    "email": "maria.aparecida@gmail.com",
    "phone": "47922222222",
    "isActive": true
}
```

#### Inativa um cliente

```http
  DELETE http://localhost:8080/customers/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do cliente |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/customers/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

</details>

<details>
  <summary>
    <strong>Ajuste</strong>
  </summary>

#### Retorna todos os ajustes

```http
  GET http://localhost:8080/adjusts
```

##### Parametros da rota
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome do ajuste |
| `isActive` | `boolean` | *Opcional*. Valor boleano que indica se o ajuste está ativo ou não |

##### Exemplo de resposta:
```json
  {
    "totalPages": 1,
    "totalElements": 2,
    "content": [
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Bainha",
            "cost": 20.0,
            "isActive": true
        },
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Ajuste de manga",
            "cost": 30.0,
            "isActive": false
        }
    ]
}
```

#### Retorna um ajuste

```http
  GET http://localhost:8080/adjusts/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do ajuste |

##### Exemplo de requisição:
```http
  GET http://localhost:8080/customers/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Bainha",
    "cost": 20.0,
    "isActive": true
}
```

#### Cria um ajuste

```http
  POST http://localhost:8080/adjusts
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | **Obrigatório**. Nome do ajuste |
| `cost` | `string` | **Obrigatório**. Custo do ajuste |

##### Exemplo de criação de um ajuste:
```json
{
    "name": "Ajuste de cintura",
    "cost": 20.0
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Ajuste de cintura",
    "cost": 20.0,
    "isActive": true
}
```

#### Atualiza os dados de um ajuste

```http
  PUT http://localhost:8080/adjusts/{id} 
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do ajuste |

##### Exemplo de requisição:
```http
  PUT http://localhost:8080/adjusts/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome do ajuste |
| `cost` | `string` | *Opcional*. Custo do ajuste |

##### Exemplo de requisição:
```json
{
    "name": "Ajuste de comprimento",
    "cost": 25.0
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Ajuste de comprimento",
    "cost": 25.0
    "isActive": true
}
```

#### Inativa um ajuste

```http
  DELETE http://localhost:8080/adjusts/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do ajuste |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/adjusts/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

</details>

<details>
  <summary>
    <strong>Medida</strong>
  </summary>

#### Retorna todos as medidas

```http
  GET http://localhost:8080/measures
```

##### Parametros da rota
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome da medida |
| `isActive` | `boolean` | *Opcional*. Valor boleano que indica se a medida está ativa ou não |

##### Exemplo de resposta:
```json
  {
    "totalPages": 1,
    "totalElements": 2,
    "content": [
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Quadril",
            "isActive": true
        },
        {
            "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
            "name": "Cintura",
            "isActive": false
        }
    ]
}
```

#### Retorna uma medida

```http
  GET http://localhost:8080/measures/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação da medida |

##### Exemplo de requisição:
```http
  GET http://localhost:8080/measures/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Quadril",
    "isActive": true
}
```

#### Cria uma medida

```http
  POST http://localhost:8080/measures
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | **Obrigatório**. Nome da medida |

##### Exemplo de criação de uma medida:
```json
{
    "name": "Cintura"
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Cintura",
    "isActive": true
}
```

#### Atualiza os dados de uma medida

```http
  PUT http://localhost:8080/measures/{id} 
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação da medida |

##### Exemplo de requisição:
```http
  PUT http://localhost:8080/measures/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | *Opcional*. Nome da medida |

##### Exemplo de requisição:
```json
{
    "name": "Punho"
}
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "name": "Punho",
    "isActive": true
}
```

#### Inativa uma medida

```http
  DELETE http://localhost:8080/measures/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação da medida |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/measures/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

</details>

<details>
  <summary>
    <strong>Pedido</strong>
  </summary>

#### Retorna todos os pedidos

```http
  GET http://localhost:8080/orders
```

##### Parametros da rota
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderNumber` | `string` | *Opcional*. Número do pedido |
| `createdAt` | `string` | *Opcional*. Data de criação do pedido |
| `finishedAt` | `string` | *Opcional*. Data de finalização do pedido |
| `customerName` | `string` | *Opcional*. Nome do cliente vinculado ao pedido |
| `customerEmail` | `string` | *Opcional*. Email do cliente vinculado ao pedido |
| `isActive` | `boolean` | *Opcional*. Valor boleano que indica se o pedido está ativo ou não |

##### Exemplo de resposta:
```json
  {
    "totalPages": 1,
    "totalElements": 2,
    "content": [
        {
            "id": "7d1739a5-7963-4977-8f8b-446798bc344f",
            "orderNumber": 1,
            "cost": null,
            "createdAt": "2024-01-10T10:00:00",
            "updatedAt": null,
            "finishedAt": "2024-01-25T17:30:00",
            "customer": {
                "name": "John Doe",
                "email": "john@example.com"
            },
            "isActive": true
        },
        {
            "id": "2d2bb2e0-2d66-4e69-a1f2-4d68d0a8b37a",
            "orderNumber": 2,
            "cost": null,
            "createdAt": "2024-01-10T10:00:00",
            "updatedAt": null,
            "finishedAt": null,
            "customer": {
                "name": "Alice Smith",
                "email": "alice@example.com"
            },
            "isActive": false
        }
    ]
}
```

#### Retorna um pedido

```http
  GET http://localhost:8080/orders/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do pedido |

##### Exemplo de requisição:
```http
  GET http://localhost:8080/orders/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Exemplo de resposta:
```json
{
    "id": "3d34c216-0e60-4a7e-89e6-f0fe8050d883",
    "orderNumber": 10,
    "createdAt": "2024-01-10T10:00:00",
    "updatedAt": null,
    "finishedAt": null,
    "customer": {
        "id": "55f490d7-02db-4e01-ae11-e8c28007f25a",
        "name": "Emma Wilson",
        "email": "emma@example.com",
        "phone": "88899900011",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "f8763fb9-96a2-496c-9382-6b24bf3bf10b",
            "type": "TAILORED",
            "title": "Vestido longo",
            "description": "Feito sob medida para baile",
            "cost": 400.0,
            "createdAt": "2024-01-10T10:00:00",
            "dueDate": "2024-01-20T10:00:00",
            "deliveredAt": "2024-01-15T10:00:00",
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "32408dfc-37a4-4e67-9a15-4f8360e68e88",
                    "measure": {
                        "id": "06f5a18c-6982-4bb0-bbf8-5f646d00bbbf",
                        "name": "Largura da gola",
                        "isActive": true
                    },
                    "measurementValue": 12.5,
                    "isActive": true
                },
                {
                    "id": "2ec9a63d-0633-4d16-9de3-c3e1cc14cc10",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 86.0,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Cria um pedido

```http
  POST http://localhost:8080/orders
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `customerId` | `string` | **Obrigatório**. Identificação do cliente |
| `items` | `array` | **Obrigatório**. Lista de itens do pedido |

##### Exemplo de criação de um pedido:
```json
{
    "customerId": "c8b5f144-545f-4c19-bf4b-9f2a8015f90b",
    "items": [
        {
            "type": "ADJUST",
            "title": "Blusa Teste",
            "description": "Blusa Teste",
            "dueDate": "2024-03-27T16:30:00",
            "cost": null,
            "adjusts": [
                {
                    "adjustmentId": "bd7e9753-9111-4391-9e91-67ff988060cc"
                }
            ]
        },
        {
            "type": "TAILORED",
            "title": "Vestido Teste",
            "description": "Vestido Teste",
            "dueDate": "2024-03-27T16:30:00",
            "cost": 350.0,
            "measures": [
                {
                    "measurementId": "b76e7857-5f43-4ab8-a966-a94ab4876613",
                    "measurementValue": 67
                },
                {
                    "measurementId": "2f4035e8-bf5a-4795-b6c5-301d1a4aa92b",
                    "measurementValue": 40
                },
                {
                    "measurementId": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                    "measurementValue": 75
                }
            ]
        }
    ]
}
```

##### Exemplo de resposta:
```json
{
    "id": "c8907625-d06f-4d81-8b5e-66e33f443fb1",
    "orderNumber": 29,
    "createdAt": "2024-03-27T16:30:00",
    "updatedAt": null,
    "finishedAt": null,
    "customer": {
        "id": "ebb3ec44-cb1f-4a43-a362-ffde86663d09",
        "name": "Bob Johnson",
        "email": "bob@example.com",
        "phone": "55566677788",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "d3d00277-fed2-4237-9266-f77e2b392411",
            "type": "TAILORED",
            "title": "Vestido Teste",
            "description": "Vestido Teste",
            "cost": 350.0,
            "createdAt": "2024-03-27T16:30:00",
            "dueDate": "2024-04-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "8f2309f6-97f9-412f-9424-3096b2b031a0",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 75.0,
                    "isActive": true
                },
                {
                    "id": "a736571b-ccdd-46ef-bc07-59ccca8e124d",
                    "measure": {
                        "id": "2f4035e8-bf5a-4795-b6c5-301d1a4aa92b",
                        "name": "Comprimento da perna",
                        "isActive": true
                    },
                    "measurementValue": 40.0,
                    "isActive": true
                },
                {
                    "id": "6505dab3-2452-456f-868a-c6c7f67444ea",
                    "measure": {
                        "id": "b76e7857-5f43-4ab8-a966-a94ab4876613",
                        "name": "Comprimento da manga",
                        "isActive": true
                    },
                    "measurementValue": 67.0,
                    "isActive": true
                }
            ]
        },
        {
            "id": "e2f17352-d1fe-43ba-9761-22612127ea86",
            "type": "ADJUST",
            "title": "Blusa Teste",
            "description": "Blusa Teste",
            "cost": 15.75,
            "createdAt": "2024-03-27T16:30:00",
            "dueDate": "2024-04-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerAdjusts": [
                {
                    "id": "df1be59d-119c-45a8-88fa-16b830bf037f",
                    "adjust": {
                        "id": "bd7e9753-9111-4391-9e91-67ff988060cc",
                        "name": "Ajuste de punho",
                        "cost": 15.75,
                        "isActive": true
                    },
                    "adjustmentCost": 15.75,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Atualiza os dados de um pedido

```http
  PUT http://localhost:8080/orders/{id} 
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do pedido |

##### Exemplo de requisição:
```http
  PUT http://localhost:8080/orders/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `customerId` | `string` | *Obrigatório*. Identificação do cliente |

##### Exemplo de requisição:
```json
{
    "customerId": "55f490d7-02db-4e01-ae11-e8c28007f25a"
}
```

##### Exemplo de resposta:
```json
{
    "id": "c8907625-d06f-4d81-8b5e-66e33f443fb1",
    "orderNumber": 29,
    "createdAt": "2024-03-27T16:30:00",
    "updatedAt": null,
    "finishedAt": null,
    "customer": {
        "id": "55f490d7-02db-4e01-ae11-e8c28007f25a",
        "name": "Bob Johnson",
        "email": "bob@example.com",
        "phone": "55566677788",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "d3d00277-fed2-4237-9266-f77e2b392411",
            "type": "TAILORED",
            "title": "Vestido Teste",
            "description": "Vestido Teste",
            "cost": 350.0,
            "createdAt": "2024-03-27T16:30:00",
            "dueDate": "2024-04-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "8f2309f6-97f9-412f-9424-3096b2b031a0",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 75.0,
                    "isActive": true
                },
                {
                    "id": "a736571b-ccdd-46ef-bc07-59ccca8e124d",
                    "measure": {
                        "id": "2f4035e8-bf5a-4795-b6c5-301d1a4aa92b",
                        "name": "Comprimento da perna",
                        "isActive": true
                    },
                    "measurementValue": 40.0,
                    "isActive": true
                },
                {
                    "id": "6505dab3-2452-456f-868a-c6c7f67444ea",
                    "measure": {
                        "id": "b76e7857-5f43-4ab8-a966-a94ab4876613",
                        "name": "Comprimento da manga",
                        "isActive": true
                    },
                    "measurementValue": 67.0,
                    "isActive": true
                }
            ]
        },
        {
            "id": "e2f17352-d1fe-43ba-9761-22612127ea86",
            "type": "ADJUST",
            "title": "Blusa Teste",
            "description": "Blusa Teste",
            "cost": 15.75,
            "createdAt": "2024-03-27T16:30:00",
            "dueDate": "2024-04-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerAdjusts": [
                {
                    "id": "df1be59d-119c-45a8-88fa-16b830bf037f",
                    "adjust": {
                        "id": "bd7e9753-9111-4391-9e91-67ff988060cc",
                        "name": "Ajuste de punho",
                        "cost": 15.75,
                        "isActive": true
                    },
                    "adjustmentCost": 15.75,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Inativa um pedido

```http
  DELETE http://localhost:8080/orders/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do pedido |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/orders/3d34c216-0e60-4a7e-89e6-f0fe8050d883
```

</details>

<details>
  <summary>
    <strong>Item Pedido</strong>
  </summary>

#### Cria um item de pedido de roupa sob medida

```http
  POST http://localhost:8080/orders/{id}/items/
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do pedido |

##### Exemplo de requisição:
```http
  POST http://localhost:8080/orders/8b2515e8-ce99-4f51-b0fb-34662f0d20c8/items/
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `type` | `string` | **Obrigatório**. Tipo do item |
| `title` | `string` | **Obrigatório**. Título do item |
| `description` | `string` | **Opcional**. Descrição do item |
| `dueDate` | `string` | **Obrigatório**. Vencimento do item |
| `cost` | `double` | **Obrigatório**. Custo do item |
| `measures` | `array` | **Opcional**. Medidas do cliente |

##### Exemplo de criação de um item de pedido de roupa sob medida:
```json
{
    "type": "TAILORED",
    "title": "Vestido",
    "description": "Vestido Vermelho",
    "dueDate": "2024-01-27T16:30:00",
    "cost": 300,
    "measures": [
        {
            "measurementId": "b76e7857-5f43-4ab8-a966-a94ab4876613",
            "measurementValue": 67
        },
        {
            "measurementId": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
            "measurementValue": 75
        }
    ]
}
```

##### Exemplo de resposta:
```json
{
    "id": "17692c22-8e3b-47f4-aa0e-5b0f3f875b17",
    "orderNumber": 10,
    "createdAt": "2024-01-27T16:30:00",
    "updatedAt": "2024-01-27T16:30:00",
    "finishedAt": null,
    "customer": {
        "id": "55f490d7-02db-4e01-ae11-e8c28007f25a",
        "name": "Emma Wilson",
        "email": "emma@example.com",
        "phone": "88899900011",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "d9ae3a8f-c481-4b6c-adb0-d2e569a87ff0",
            "type": "TAILORED",
            "title": "Vestido",
            "description": "Vestido Vermelho",
            "cost": 300.0,
            "createdAt": "2024-01-27T16:30:00",
            "dueDate": "2024-01-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "97dfd62b-3014-4c77-b322-a6c59f8cb5ed",
                    "measure": {
                        "id": "b76e7857-5f43-4ab8-a966-a94ab4876613",
                        "name": "Comprimento da manga",
                        "isActive": true
                    },
                    "measurementValue": 67.0,
                    "isActive": true
                },
                {
                    "id": "b4d88c69-ceb0-4b0b-ac88-171d502ed66b",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 75.0,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Cria um item de pedido de ajuste de roupa

```http
  POST http://localhost:8080/orders/{id}/items/
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `id` | `string` | **Obrigatório**. Identificação do pedido |

##### Exemplo de requisição:
```http
  POST http://localhost:8080/orders/8b2515e8-ce99-4f51-b0fb-34662f0d20c8/items/
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `type` | `string` | **Obrigatório**. Tipo do item |
| `title` | `string` | **Obrigatório**. Título do item |
| `description` | `string` | **Opcional**. Descrição do item |
| `dueDate` | `string` | **Obrigatório**. Vencimento do item |
| `adjusts` | `array` | **Opcional**. Ajustes do cliente |

##### Exemplo de criação de um item de pedido de ajuste de roupa:
```json
{
    "type": "ADJUST",
    "title": "Blusa",
    "description": "Blusa rasgada",
    "dueDate": "2024-01-27T16:30:00",
    "adjusts": [
        {
            "adjustmentId": "bd7e9753-9111-4391-9e91-67ff988060cc"
        },
        {
            "adjustmentId": "2f4035e8-bf5a-4795-b6c5-301d1a4aa92b"
        }
    ]
}
```

##### Exemplo de resposta:
```json
{
    "id": "17692c22-8e3b-47f4-aa0e-5b0f3f875b17",
    "orderNumber": 10,
    "createdAt": "2024-01-27T16:30:00",
    "updatedAt": "2024-01-27T16:30:00",
    "finishedAt": null,
    "customer": {
        "id": "55f490d7-02db-4e01-ae11-e8c28007f25a",
        "name": "Emma Wilson",
        "email": "emma@example.com",
        "phone": "88899900011",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "d9ae3a8f-c481-4b6c-adb0-d2e569a87ff0",
            "type": "ADJUST",
            "title": "Blusa",
            "description": "Blusa rasgada",
            "cost": 300.0,
            "createdAt": "2024-01-27T16:30:00",
            "dueDate": "2024-01-27T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerAdjusts": [
                {
                    "id": "d0d3f2fe-8940-3345-a60e-ce68fe9fb497",
                    "adjust": {
                        "id": "bd7e9753-9111-4391-9e91-67ff988060cc",
                        "name": "Ajuste de barra",
                        "cost": 35.25,
                        "isActive": true
                    },
                    "adjustmentCost": 35.25,
                    "isActive": true
                },
                                {
                    "id": "d0d3f2fe-8940-3345-a60e-ce68fe9fb497",
                    "adjust": {
                        "id": "2f4035e8-bf5a-4795-b6c5-301d1a4aa92b",
                        "name": "Ajuste de barra",
                        "cost": 35.25,
                        "isActive": true
                    },
                    "adjustmentCost": 35.25,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Atualiza os dados de um item de pedido

```http
  PUT http://localhost:8080/orders/{orderId}/items/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**. Identificação do pedido |
| `id` | `string` | **Obrigatório**. Identificação do item |

##### Exemplo de requisição:
```http
  PUT http://localhost:8080/orders/8b2515e8-ce99-4f51-b0fb-34662f0d20c8/items/cf1c07a2-7685-4870-9878-8afe625e7b47
```

##### Corpo da requisição:
| Chave   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `title` | `string` | *Opcional*. Titulo do item |
| `description` | `string` | *Opcional*. Descrição do item |
| `dueDate` | `string` | *Opcional*. Vencimento do item |
| `cost` | `double` | *Opcional*. Custo do item. Se o item for do tipo 'Ajuste de Roupa' este campo não pode ser editado, pois é calculado automaticamente |

##### Exemplo de requisição:
```json
{
    "title": "Vestido Atualizado",
    "description": "",
    "dueDate": "2024-01-30T16:30:00",
    "cost": 353.0
}
```

##### Exemplo de resposta:
```json
{
    "id": "34e6b5c7-6f5a-4e95-a5de-cd4ed3a45623",
    "orderNumber": 10,
    "createdAt": "2024-01-30T16:30:00",
    "updatedAt": "2024-01-30T16:30:00",
    "finishedAt": null,
    "customer": {
        "id": "55f490d7-02db-4e01-ae11-e8c28007f25a",
        "name": "Emma Wilson",
        "email": "emma@example.com",
        "phone": "88899900011",
        "isActive": true
    },
    "orderItems": [
        {
            "id": "d9ae3a8f-c481-4b6c-adb0-d2e569a87ff0",
            "type": "TAILORED",
            "title": "Vestido Atualizado",
            "description": "",
            "cost": 353.0,
            "createdAt": "2024-01-30T16:30:00",
            "dueDate": "2024-01-30T16:30:00",
            "deliveredAt": null,
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "97dfd62b-3014-4c77-b322-a6c59f8cb5ed",
                    "measure": {
                        "id": "b76e7857-5f43-4ab8-a966-a94ab4876613",
                        "name": "Comprimento da manga",
                        "isActive": true
                    },
                    "measurementValue": 67.0,
                    "isActive": true
                },
                {
                    "id": "b4d88c69-ceb0-4b0b-ac88-171d502ed66b",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 75.0,
                    "isActive": true
                }
            ]
        },
        {
            "id": "f8763fb9-96a2-496c-9382-6b24bf3bf10b",
            "type": "TAILORED",
            "title": "Vestido longo",
            "description": "Feito sob medida para baile",
            "cost": 400.0,
            "createdAt": "2024-01-30T16:30:00",
            "dueDate": "2024-01-30T16:30:00",
            "deliveredAt": "2024-01-30T16:30:00",
            "isActive": true,
            "customerMeasures": [
                {
                    "id": "32408dfc-37a4-4e67-9a15-4f8360e68e88",
                    "measure": {
                        "id": "06f5a18c-6982-4bb0-bbf8-5f646d00bbbf",
                        "name": "Largura da gola",
                        "isActive": true
                    },
                    "measurementValue": 12.5,
                    "isActive": true
                },
                {
                    "id": "2ec9a63d-0633-4d16-9de3-c3e1cc14cc10",
                    "measure": {
                        "id": "23a1102d-2023-4efa-898e-0d45f69ad5fa",
                        "name": "Busto",
                        "isActive": true
                    },
                    "measurementValue": 86.0,
                    "isActive": true
                }
            ]
        }
    ],
    "isActive": true
}
```

#### Inativa um item de pedido

```http
  DELETE http://localhost:8080/orders/{orderId}/items/{id}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**. Identificação do pedido |
| `id` | `string` | **Obrigatório**. Identificação do item |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/orders/a34aa124-4375-41a5-b417-fb884d485af2/items/f94bdbfb-2e47-4fb4-8854-d60d25c10a33
```

##### Exemplo de resposta:
```json
{
    "id": "f94bdbfb-2e47-4fb4-8854-d60d25c10a33",
    "type": "TAILORED",
    "title": "Vestido Teste",
    "description": "Vestido Teste",
    "cost": 350.0,
    "createdAt": "2024-03-27T16:30:00",
    "dueDate": "2024-04-27T16:30:00",
    "deliveredAt": null,
    "isActive": false,
    "customerMeasures": null
}
```

#### Entrega um item de pedido

```http
  PATCH http://localhost:8080/orders/{prderId}/items/{id}/deliver
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**. Identificação do pedido |
| `id` | `string` | **Obrigatório**. Identificação do item |

##### Exemplo de requisição:
```http
  DELETE http://localhost:8080/orders/a34aa124-4375-41a5-b417-fb884d485af2/items/f94bdbfb-2e47-4fb4-8854-d60d25c10a33/deliver
```

</details>
