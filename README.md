# todo-list

## A solução
Foi criado uma RESTFUL API simples em Java que armazena e atualiza tarefas (TODO-LIST API)

## Instalação
- Pré-requisitos
    - Java 11 ou acima
    - Gradle ou use o wrapper ./gradlew

## Tecnologias utilizadas
    
- Spring Boot 2.5.0 (https://spring.io/blog/2021/05/20/spring-boot-2-5-is-now-ga)
    - Spring Data
    - Spring Security
    - Spring Sleuth
    - Spring Actuator
- H2 Database (https://www.h2database.com/html/main.html)
- JSON Web Token (https://jwt.io/)
- Springfox (https://springfox.github.io/springfox/)

## Instruções para executar

1. Baixe o projeto.

```shell
git clone git@github.com:andersonszasilva/todo-list.git
```

2. Rode os testes utilizando o comando abaixo:

```shell
./gradlew test
```

3. Construa o projeto.

```shell
./gradlew build
```

4. Para executar a aplicação pelo Docker utilize os comandos a seguir:

```shell
docker build -f docker/local.dockerfile -t todo-list . && docker run -p 8080:8080 todo-list
```
- **Obs.:** Caso queira rodar a aplicação pelo gradle mesmo utilize o comando abaixo:

```
./gradlew bootRun
```
## Realizando chamadas para a aplicação

No banco de dados da aplicação há 3 usuários pré-cadastrados, com os seguintes dados para acesso:

1º com perfil de autorização de usuário comum
```json
{
 "username":"wile.coyote@acmecorporation.com",
 "password": "123456"
}
```
2º também com perfil de autorização para um usuário comum
```json
{
"username":"road.runner@acmecorporation.com",
"password": "123456"
}
```
3º Usuário com autorização de super user
```json
{
"username":"super.user@acmecorporation.com",
"password": "123456"
}
```

Para efetuar o login na aplicação e recuperar o token basta executar a chamada http abaixo com qualquer destes usuários:

```shell
curl --location --request POST 'http://localhost:8080/auth' \
--header 'Content-Type: application/json' \
--data-raw '{
"username":"wile.coyote@acmecorporation.com",
"password": "123456"
}';
```

Caso tenha digitado os dados de acesso corretamente, a aplicação retornará o estado HTTP 200 de OK e com a resposta abaixo, no caso das informações estarem incorretas a aplicação retornará um estado HTTP 4OO de BAD REQUEST.
```json
{
"token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88",
"type": "Bearer",
"expirationDate": "2021-06-11 01:05:35"
}
```

Com a resposta em mãos o usuário deve utilizar o token Bearer para as chamadas na API, depois de 5 minutos o token é expirado e usuário dever fazer uma nova autenticação.

### Realizando as chamadas para as funções desejadas sobre a tarefa com o token Bearer

Para criar uma nova tarefa execute com o comando abaixo:

```shell
curl --location --request POST 'http://localhost:8080/todo' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88' \
--header 'Content-Type: application/json' \
--data-raw '{

  "summary": "Ir ao mercado",
  "description": "Comprar cervejas"

}';
```

Então é criado uma nova tarefa e o estado HTTP retornado é o 201 de CREATED e com a resposta abaixo:

```json
{
    "id": 1,
    "summary": "Ir ao mercado",
    "description": "Comprar cervejas",
    "status": "PENDING",
    "createDate": "2021-06-11 01:00:44"
}
```
Para alterar o estado da tarefa de "PENDING" para "COMPLETED" ou vice-versa, poderá ser feito com a seguinte chamada: 

**Atenção:** a aplicação de todo-list foi projetado com a funcionalidade de finalizar e retroceder a tarefa, assim como fazemos com o aplicativo do Google Keeper de TODO, já que, no escopo não estava claro sobre esta funcionalidade.

```shell
curl --location --request PATCH 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzM3MzIzNSwiZXhwIjoxNjIzMzczNTM1fQ.0zk7DZUxFsocaY1E8sdEFAs3GMGgiQumGFoNOy_5H88' \
--header 'Content-Type: application/json' \
--data-raw '{
    "status":"COMPLETED"
}'
```

O usuário também poderá listar todas as suas tarefas, ordenadas sempre pelo estado de "PENDING".

```shell
curl --location --request GET 'http://localhost:8080/todo' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDA2OCwiZXhwIjoxNjIzNDEwMzY4fQ.IhSWkRtPbJ2QksmicF8WTXAWgeuPrN13kcvQIyedkto' \
--data-raw ''
```

A resposta será desta maneira:

``` json
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

O usuário também poderá listar as tarefas filtrando pelos estados com as seguintes chamadas:  ```http://localhost:8080/todo?status=COMPLETED ``` e ```http://localhost:8080/todo?status=PENDING ```


Somente o próprio usuário que criou as tarefas poderá visualizá-las, mas a aplicação também tem um usuário com o perfil de "super user" e com ele autenticado é possível ver as tarefas de outros usuários.

Para isso devemos autenticar com este usuário com a seguinte chamada:

```shell
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

**Atenção:** O recurso de super user está disponível apenas para a listagem, ou seja, um super user não conseguirá alterar ou remover a tarefa de outro usuário.

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

Na aplicação também estão disponíveis as funcionalidades de visualizar, editar(resumo e descrição da tarefa) e de remover, com as seguintes chamadas:

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
Remover:

```
curl --location --request DELETE 'http://localhost:8080/todo/1' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJUT0RPIEFQSSBMSVNUIiwic3ViIjoiMSIsImlhdCI6MTYyMzQxMDkzOCwiZXhwIjoxNjIzNDExMjM4fQ.riUufR9pKFR2kFS5jQlEoWSpf7FvKdCUc6UJ5BUzMV0' \
--data-raw ''
```

## Monitoramento

A aplicação também oferece um jeito muito fácil para analisar o seu estado, para isso basta fazer uma chamada para o seguinte endereço:

```shell
curl --location --request GET 'http://localhost:8080/actuator/health' 
```
Então, a aplicação retornará com os seguintes dados sobre o seu estado e de seus componentes envolvidos.

```json
{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "H2",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 234685313024,
                "free": 153060208640,
                "threshold": 10485760,
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        }
    }
}
```

Caso tenha ficado alguma dúvida fique à vontade para explorar mais sobre a api na documentação a seguir: ``` http://localhost:8080/swagger-ui.html ``` ou pelas collections do Postman na pasta "postman" dentro do projeto.

Obrigado!

## That's all folks!