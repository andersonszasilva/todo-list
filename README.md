# todo-list

## A solução
Foi criado uma RESTFUL API simples em Java que armazena e atualiza tarefas (TODO-LIST API)

## Instalação
- Pré-requisitos
    - Java 11 ou acima
    - Gradle ou use o wrapper ./gradlew

## Tecnologias utilizadas
    
- Spring Boot 2.5.0
    - Spring Data
    - Spring Security
    - Spring Sleuth
- H2 Database
- JSON Web Token

## Instruções para executar

1. Baixe o projeto

```
git clone git@github.com:andersonszasilva/todo-list.git
```

2. Para rodar os testes utilize o script abaixo

```
./gradlew test
```

3. Para construir o projeto utilize o script abaixo

```
./gradlew build
```

4. Para executar a aplicação pelo Docker utilize os comando

```
docker build -f docker/local.dockerfile -t todo-list . && docker run -p 8080:8080 todo-list
```
- Obs.: Caso queira rodar a aplicação pelo gradle utilize o comando abaixo

```
./gradlew bootRun
```
## Realizando chamadas para a aplicação

Na banco de dados da aplicação há 3 usuários pré-cadastrados, com seguintes dados para acesso:

1º com perfil de autorização de usuário comum
```
{
 "username":"wile.coyote@acmecorporation.com",
 "password": "123456"
}
```
2º também com perfil de autorização para um usuário comum
```
{
"username":"road.runner@acmecorporation.com",
"password": "123456"
}
```
3º Usuário com autorização de super user
```
{
"username":"super.user@acmecorporation.com",
"password": "123456"
}
```

Para efetuar o login na aplicação e recuperar o token basta executar a chamada http abaixo com qualquer deste usuários

```
curl --location --request POST 'http://localhost:8080/auth' \
--header 'Content-Type: application/json' \
--data-raw '{
"username":"wile.coyote@acmecorporation.com",
"password": "123456"
}';
```

Caso você tenha digitado os dados de acesso corretamente, aplicação retornará o estado HTTP 200 de OK e com a resposta abaixo, no caso das informações estarem incorretas a aplicação retornará um estado HTTP 4OO de BAD REQUEST.
```
{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88",
"type": "Bearer",
"expirationDate": "2021-06-11 01:05:35"
}
```

Com a resposta em mãos o usuário deve utilizar o token Bearer para as chamadas na API, depois de 5 minutos o token é expirado e usuário dever fazer uma nova autenticação

### Realizando as chamadas para as funções desejadas sobre a tarefa com o token Bearer

Para criar uma nova tarefa execute com o comando abaixo:

```
curl --location --request POST 'http://localhost:8080/todo' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88' \
--header 'Content-Type: application/json' \
--data-raw '{

  "summary": "Ir ao mercado",
  "description": "Comprar cervejas"
  
}';
```

Então é criado uma nova tarefa com estado, o estado HTTP retornado é o 201 de CREATED e com a resposta abaixo:

```
{
    "id": 1,
    "summary": "Ir ao mercado",
    "description": "Comprar cervejas",
    "status": "PENDING",
    "createDate": "2021-06-11 01:00:44"
}
```
Para alterar o estado da tarefa de "PENDING" para "Completed" ou vice-versa, você realizar a seguinte chamada: 

Atenção: a aplicação de todo-list foi projetado com a funcionalidade de finalizar e reveter a tarefa, assim como fazemos com o aplicativo do Google Keeper de todo, já que, no escopo não estava claro sobre esta funcionalidade.

```
curl --location --request PATCH 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88' \
--header 'Content-Type: application/json' \
--data-raw '{
    "status":"COMPLETED"
}'
```

O usuário também poderá listar todas as suas tarefas ordenadas pelo estado "PENDING" primeiro com a seguinte chamada:

```
curl --location --request GET 'http://localhost:8080/todo' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDA2OCwiZXhwIjoxNjIzNDEwMzY4fQ.IhSWkRtPbJ2QksmicF8WTXAWgeuPrN13kcvQIyedkto' \
--data-raw ''
```

A resposta será desta maneira:

``` 
[
    {
        "id": 2,
        "summary": "Ir ao barbeiro",
        "description": "Fazer corte de barba e cabelo",
        "status": "PENDING",
        "createDate": "2021-06-11 08:15:30"
    },
    {
        "id": 1,
        "summary": "Ir ao mercado",
        "description": "Comprar cervejas",
        "status": "COMPLETED",
        "createDate": "2021-06-11 08:14:30"
    }
]

```

Um usuário também poderá listar as tarefas filtrando pelo estados com as chamadas:  ```http://localhost:8080/todo?status=COMPLETED ``` e ```http://localhost:8080/todo?status=PENDING ```


Somente o próprio usuário que criou as tarefas poderá visualizá-las, mas a aplicação também tem o usuário com o perfil de "super user" e com ele autenticado é possível ver as tarefas de outros usuários.

Para isso devemos autenticar com este usuário com a seguinte chamada:

```
curl --location --request POST 'http://localhost:8080/auth' \
--header 'Content-Type: application/json' \
--data-raw '{
"username":"super.user@acmecorporation.com",
"password": "123456"
}';
```

Resposta do token:

```
{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMyIsImlhdCI6MTYyMzQxMDc4OSwiZXhwIjoxNjIzNDExMDg5fQ.Tu0qfT46QqLCN-HhUXvqlPcmBMKvvw0frgmvTeWBNZU",
"type": "Bearer",
"expirationDate": "2021-06-11 08:31:29"
}
```

E agora quando executamos a chamada para o endereço de listagem com token de super user:

```
curl --location --request GET 'http://localhost:8080/todo' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMyIsImlhdCI6MTYyMzQxMDc4OSwiZXhwIjoxNjIzNDExMDg5fQ.Tu0qfT46QqLCN-HhUXvqlPcmBMKvvw0frgmvTeWBNZU' \
--data-raw ''
```

Podemos ver as tarefas listadas por outros usuários:

```
[
    {
        "id": 2,
        "summary": "Ir ao barbeiro",
        "description": "Fazer corte de barba e cabelo",
        "status": "PENDING",
        "createDate": "2021-06-11 08:15:30",
        "user": {
            "id": 1,
            "username": "wile.coyote@acmecorporation.com"
        }
    },
    {
        "id": 3,
        "summary": "Fazer o teste",
        "description": "Teste prático",
        "status": "PENDING",
        "createDate": "2021-06-11 08:29:06",
        "user": {
            "id": 2,
            "username": "road.runner@acmecorporation.com"
        }
    },
    {
        "id": 1,
        "summary": "Ir ao mercado",
        "description": "Comprar cervejas",
        "status": "COMPLETED",
        "createDate": "2021-06-11 08:14:30",
        "user": {
            "id": 1,
            "username": "wile.coyote@acmecorporation.com"
        }
    }
]
```

Para aplicação também oferecemos as funcionalidades extras de visualizar, editar(resumo e descrição da tarefa) e de remoção, com as seguintes chamadas:

Visualizar tarefa:
```
curl --location --request GET 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDkzOCwiZXhwIjoxNjIzNDExMjM4fQ.riUufR9pKFR2kFS5jQlEoWSpf7FvKdCUc6UJ5BUzMV0' \
--data-raw ''
```
Editar:

```
curl --location --request PUT 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDkzOCwiZXhwIjoxNjIzNDExMjM4fQ.riUufR9pKFR2kFS5jQlEoWSpf7FvKdCUc6UJ5BUzMV0' \
--header 'Content-Type: application/json' \
--data-raw '{

  "summary": "Ir ao mercado da esquina",
  "description": "Comprar cervejas e leite"

}'
```
Remoção:

```
curl --location --request DELETE 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDkzOCwiZXhwIjoxNjIzNDExMjM4fQ.riUufR9pKFR2kFS5jQlEoWSpf7FvKdCUc6UJ5BUzMV0' \
--data-raw ''
```