# DSCommerce

Sistema de e-commerce desenvolvido como projeto estruturado do curso **Java Spring Professional** da [DevSuperior](https://devsuperior.com.br), com autenticação OAuth2/JWT e controle de acesso por perfil de usuário.

## Sobre o projeto

O DSCommerce é uma API REST de e-commerce que permite gerenciar produtos, categorias e pedidos. A visualização de produtos é pública, enquanto operações administrativas exigem perfil ADMIN. Qualquer usuário autenticado pode realizar pedidos.

## Modelo de domínio

- **User** → possui perfis (roles): `ROLE_OPERATOR` e `ROLE_ADMIN`
- **Product** → pertence a uma ou mais categorias
- **Order** → realizado por um usuário, contém itens de pedido
- **OrderItem** → associação entre pedido e produto com quantidade e preço

## Tecnologias utilizadas

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security
- OAuth2 Authorization Server
- JWT (JSON Web Token)
- H2 Database (perfil de teste)
- Bean Validation
- Maven

## Funcionalidades

- ✅ Listagem paginada de produtos (público)
- ✅ Busca de produto por ID (público)
- ✅ Inserção, atualização e deleção de produto (somente ADMIN)
- ✅ Listagem de categorias (público)
- ✅ Login com retorno de token de acesso JWT
- ✅ Endpoint `/users/me` retorna dados do usuário logado
- ✅ Criação e consulta de pedidos (usuário autenticado)
- ✅ Controle de acesso: usuário não-ADMIN não acessa pedido de outro usuário
- ✅ Tratamento de exceções com respostas HTTP customizadas
- ✅ Validação de dados com Bean Validation

## Controle de acesso

| Endpoint | Público | OPERATOR | ADMIN |
|---|---|---|---|
| GET /products | ✅ | ✅ | ✅ |
| GET /products/{id} | ✅ | ✅ | ✅ |
| POST/PUT/DELETE /products | ❌ | ❌ | ✅ |
| GET /categories | ✅ | ✅ | ✅ |
| GET /users/me | ❌ | ✅ | ✅ |
| GET/POST /orders | ❌ | ✅ | ✅ |

## Como executar

```bash
# Clone o repositório
git clone https://github.com/pedrotrevisan/dscommerce.git

# Entre na pasta
cd dscommerce

# Execute o projeto
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

H2 Console: `http://localhost:8080/h2-console`

## Login de teste

```
# Usuário OPERATOR
username: maria@gmail.com
password: 123456

# Usuário ADMIN
username: alex@gmail.com
password: 123456
```

## Autor

**Pedro Henrique Trevisan**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Pedro_Trevisan-blue)](https://www.linkedin.com/in/pedrotrevisan)
[![GitHub](https://img.shields.io/badge/GitHub-pedrotrevisan-black)](https://github.com/pedrotrevisan)
