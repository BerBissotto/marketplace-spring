# Marketplace

![Versão](https://img.shields.io/badge/vers%C3%A3o-1.2.0--SNAPSHOT-0d6efd)
![Java](https://img.shields.io/badge/Java-21-e76f00)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-6db33f)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-compat%C3%ADvel-4169e1)

Aplicação web de marketplace construída com Spring Boot, Thymeleaf e PostgreSQL. O sistema reúne uma vitrine pública de produtos, cadastro e autenticação de clientes, carrinho, cálculo de frete, embalagem e desconto, checkout com métodos de pagamento simulados e uma área de administração.

> Versão atual: **1.2.0-SNAPSHOT**. O projeto usa **Java 21**, conforme definido no `pom.xml`.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Arquitetura e padrões](#arquitetura-e-padrões)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e execução](#configuração-e-execução)
- [Acesso administrativo](#acesso-administrativo)
- [Testes e build](#testes-e-build)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Rotas principais](#rotas-principais)
- [Observações importantes](#observações-importantes)

## Funcionalidades

### Loja e produtos

- Vitrine pública com produtos em destaque.
- Catálogo paginado com filtros por nome, categoria e faixa de preço.
- Página de detalhes com galeria de imagens.
- Cadastro, edição, listagem e exclusão de produtos.
- Upload de múltiplas imagens, limitado a 10 MB por arquivo e 50 MB por requisição.
- Armazenamento das imagens no PostgreSQL em `bytea` e entrega pelo endpoint `/imagem/{id}`.
- Controle de estoque atualizado após a finalização da compra.

### Usuários e segurança

- Cadastro de clientes com endereço.
- Autenticação por e-mail e senha com Spring Security.
- Senhas protegidas com BCrypt.
- Perfis `USER` e `ADMIN`.
- Recuperação do carrinho pendente após o login.

### Carrinho e checkout

- Inclusão, remoção e alteração da quantidade de produtos.
- Opções de frete econômico, padrão e expresso.
- Opções de embalagem básica, presente, comemorativa ou sem embalagem.
- Aplicação de desconto percentual configurado no pedido.
- Cálculo do total no servidor antes da persistência e do pagamento.
- Pagamentos simulados por Pix, PayPal ou boleto.
- Listagem e administração de pedidos e pagamentos.

### Interface

- Layout responsivo com Bootstrap.
- Templates HTML semânticos e componentes reutilizáveis com Thymeleaf.
- Skeletons de carregamento, lazy loading de imagens e barra de progresso.
- Animações de entrada, saída e feedback de formulários.
- Compatibilidade com `prefers-reduced-motion` e mensagens acessíveis via ARIA.

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.4 |
| Web | Spring MVC |
| Persistência | Spring Data JPA / Hibernate |
| Segurança | Spring Security 6 e BCrypt |
| Templates | Thymeleaf e Thymeleaf Extras Spring Security |
| Interface | Bootstrap 5.3.3, CSS e JavaScript |
| Banco de dados | PostgreSQL |
| Build | Maven 3.9.9 via Maven Wrapper |
| Produtividade | Lombok |
| Testes | JUnit 5 e Spring Boot Test |

## Arquitetura e padrões

O projeto segue uma arquitetura MVC em camadas:

```text
Requisição HTTP
      │
      ▼
Controller ──► Service ──► Repository ──► PostgreSQL
      │
      └──────► Template Thymeleaf ──► HTML
```

Padrões de projeto aplicados:

| Padrão | Aplicação no projeto |
| --- | --- |
| Factory | `ProdutoFactory` centraliza a criação de produtos e imagens. |
| Adapter | Os adaptadores de Pix, PayPal e boleto oferecem uma interface única de pagamento. |
| Decorator | Frete, embalagem e desconto são combinados dinamicamente no cálculo do pedido. |

Os pagamentos são apenas simulações locais. Nenhuma transação financeira real é enviada para Pix, PayPal ou instituições bancárias.

## Pré-requisitos

- JDK 21.
- PostgreSQL em execução.
- Git, caso o projeto seja obtido por um repositório.
- Não é necessário instalar o Maven globalmente: o projeto inclui o Maven Wrapper.

Confirme a versão ativa do Java:

```bash
java -version
```

O resultado deve indicar a versão 21. Em IDEs, configure também o Project SDK, o nível da linguagem e o Maven Runner para o JDK 21.

## Configuração e execução

### 1. Crie o banco de dados

No PostgreSQL, crie o banco usado pela configuração atual:

```sql
CREATE DATABASE postgres1;
```

É possível usar outro nome, desde que a URL de conexão seja atualizada.

### 2. Configure a conexão

O arquivo `src/main/resources/application.properties` lê as credenciais de variáveis de ambiente:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/postgres1}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
```

Defina `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` no seu ambiente antes de iniciar a aplicação. Não envie credenciais reais para o controle de versão. Em produção, prefira um gerenciador de segredos.

Os controles de sessão e tentativas de login também podem ser ajustados por ambiente:

```properties
SESSION_COOKIE_SECURE=false
LOGIN_MAX_ATTEMPTS=5
LOGIN_LOCK_DURATION=15m
```

Mantenha `SESSION_COOKIE_SECURE=false` somente durante o desenvolvimento em HTTP local. Em produção com HTTPS, defina o valor como `true`. O limitador de login é mantido em memória; se a aplicação executar em mais de uma instância, substitua o armazenamento por Redis ou outro serviço compartilhado.

Linux ou macOS:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/postgres1"
export DB_USERNAME="postgres"
export DB_PASSWORD="sua_senha"
```

Windows (PowerShell):

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/postgres1"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha"
```

O Hibernate está configurado com `spring.jpa.hibernate.ddl-auto=update`, portanto as tabelas são criadas ou atualizadas quando a aplicação inicia. Esse comportamento é conveniente para desenvolvimento; em produção, use migrações versionadas, como Flyway ou Liquibase.

### 3. Inicie a aplicação

Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Depois, acesse [http://localhost:8080](http://localhost:8080).

### 4. Crie uma conta

Acesse `/formcliente` ou use o botão de cadastro na tela de login. Contas criadas pela área pública recebem o perfil `USER` e a senha é armazenada com BCrypt.

## Acesso administrativo

O projeto não cria automaticamente um administrador. Para preparar o primeiro acesso:

1. Cadastre uma conta normalmente pela aplicação.
2. Altere o perfil dessa conta no PostgreSQL:

```sql
UPDATE usuario
SET role = 'ADMIN'
WHERE email = 'seu-email@exemplo.com';
```

3. Saia da aplicação, entre novamente e acesse `/admin`.

As rotas administrativas de produtos, usuários, pedidos e pagamentos exigem explicitamente o perfil `ADMIN` em `SecurityConfig`.

O checkout não aceita um identificador de pedido escolhido pelo navegador. O servidor obtém o pedido pendente a partir do usuário autenticado, revalida o estoque e finaliza pagamento, estoque e pedido na mesma transação. As respostas administrativas em JSON utilizam DTOs e não serializam as entidades completas.

## Testes e build

Execute os testes automatizados:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

A suíte cobre o carregamento do contexto Spring, a criação de produtos com imagens e o cálculo de frete, embalagem e desconto.

Para gerar o pacote executável:

```bash
./mvnw clean package
java -jar target/SpringAula2-1.2.0-SNAPSHOT.jar
```

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/example/aula10/
│   │   ├── adapter/       # métodos de pagamento
│   │   ├── config/        # segurança e login
│   │   ├── controller/    # rotas MVC e endpoints
│   │   ├── decorator/     # frete, embalagem e desconto
│   │   ├── factory/       # criação de produtos e imagens
│   │   ├── model/         # entidades JPA
│   │   ├── repository/    # acesso ao PostgreSQL
│   │   └── service/       # regras de negócio
│   └── resources/
│       ├── static/        # CSS, JavaScript e imagens fixas
│       ├── templates/     # páginas e fragmentos Thymeleaf
│       └── application.properties
└── test/                  # testes automatizados
```

## Rotas principais

| Rota | Descrição | Acesso |
| --- | --- | --- |
| `/` | Página inicial | Público |
| `/produtos` | Catálogo e filtros | Público |
| `/produtos/{id}` | Detalhes do produto | Público |
| `/formcliente` | Cadastro de cliente | Público |
| `/login` | Autenticação | Público |
| `/pedidos` | Carrinho do usuário | Autenticado |
| `/admin` | Painel administrativo | `ADMIN` |
| `/imagem/{id}` | Conteúdo binário de uma imagem | Público |

## Observações importantes

- O projeto requer Java 21. Compilar com um JDK diferente pode causar falhas do compilador ou do processador de anotações do Lombok.
- Se ocorrer `TypeTag :: UNKNOWN`, confira se a IDE está usando o JDK 21, reimporte o projeto Maven e habilite o processamento de anotações do Lombok.
- As imagens ficam no próprio banco. Para catálogos grandes, considere migrar os arquivos para um armazenamento de objetos e manter apenas as URLs no PostgreSQL.
- O cache HTTP das imagens é configurado para 30 dias.
- O Bootstrap é carregado por CDN com verificação de integridade SRI; a primeira renderização depende de acesso à internet.
- Os formulários POST incluem tokens CSRF e a proteção permanece habilitada no Spring Security.
- Credenciais locais devem ser fornecidas por variáveis de ambiente; arquivos `.env` e `application-local.properties` são ignorados pelo Git.
- Os formulários possuem validação no servidor; as validações do navegador são apenas uma camada de usabilidade.
- A aplicação envia Content Security Policy, Referrer Policy, Permissions Policy, proteção contra iframes e HSTS em respostas HTTPS.
- Dados pessoais não são incluídos nos métodos `toString()` de usuário e endereço, reduzindo a exposição acidental em logs.
- O projeto ainda não possui uma licença definida. Inclua um arquivo `LICENSE` antes de distribuí-lo publicamente.

---

Desenvolvido como uma aplicação de estudo de Spring Boot, MVC, persistência, segurança e padrões de projeto.
