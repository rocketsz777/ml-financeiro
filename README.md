<h1 align="center">💹 ml-financeiro</h1>

<p align="center">
  Sistema de gestão de vendas para e-commerce — centraliza pedidos do Mercado Livre e Shopee, controla estoque e exibe um dashboard financeiro completo.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8-ED8B00?style=flat&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-2-6DB33F?style=flat&logo=spring-boot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/PostgreSQL-Neon-316192?style=flat&logo=postgresql&logoColor=white"/>
  <img src="https://img.shields.io/badge/React-18-61DAFB?style=flat&logo=react&logoColor=black"/>
  <img src="https://img.shields.io/badge/Deploy-Render-46E3B7?style=flat&logo=render&logoColor=white"/>
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-EF9F27?style=flat"/>
</p>

---

## 📌 Sobre o projeto

O **ml-financeiro** resolve um problema real de quem vende em múltiplos marketplaces: cada plataforma tem seu próprio painel, com métricas diferentes, e cruzar os dados manualmente custa tempo e gera erros.

Este sistema conecta diretamente às APIs oficiais do **Mercado Livre** e da **Shopee** via OAuth2, importa os pedidos sob demanda por um botão no frontend, controla estoque por SKU e consolida tudo em um dashboard com gráficos e filtros por período.

> Projeto desenvolvido como portfólio técnico, baseado em um problema real do meu próprio negócio de e-commerce — uso esse sistema no dia a dia das minhas vendas.

---

## ✅ Funcionalidades

### Implementadas
- [x] Autenticação OAuth2 com Mercado Livre e Shopee
- [x] Importação de pedidos via API acionada pelo frontend
- [x] Health check da aplicação
- [x] Dashboard com faturamento, custos, lucro e total de vendas
- [x] Gráficos de performance financeira e distribuição de produtos
- [x] Ranking dos produtos mais vendidos no período
- [x] Filtros por período (diário, semanal, mensal, anual, períodos anteriores)
- [x] Filtro de vendas por marketplace
- [x] Gestão manual de produtos com controle de estoque e custo
- [x] Vínculo de vendas por SKU com cálculo de margem por marketplace
- [x] Calculadora de precificação (Mercado Livre e Shopee, com frete e impostos)
- [x] Containerização com Docker (build multistage)

### Em desenvolvimento
- [ ] Fechamento mensal em Excel
- [ ] Testes automatizados (JUnit/Mockito) na lógica de negócio
- [ ] Nova varredura SonarQube (validação das correções de segurança e code smells)
- [ ] Documentação de API com Swagger/OpenAPI
- [ ] Deploy do frontend em produção
- [ ] Indicadores de variação vs. período anterior no dashboard

---

## 🔁 Fluxo de trabalho

O desenvolvimento segue um fluxo baseado em **Git Flow**:

- `main` — código estável, em produção
- `develop` — integração das features antes de ir pra produção
- `feature/*` — uma branch por funcionalidade (ex: `feature/postgresql`, `feature/controle_estoque`), integrada via Pull Request

Endpoints são validados manualmente com **Postman** durante o desenvolvimento antes do merge.

---

## 🏗️ Arquitetura

Arquitetura monolítica em camadas, com integrações separadas para Mercado Livre e Shopee:

```
mlfinanceiro/
├── config/              # Configurações da aplicação (beans, segurança, etc.)
│
├── controller/           # Endpoints REST
│   ├── DashboardController
│   ├── HealthController
│   ├── ImportController
│   ├── ProductController
│   ├── SalesController
│   └── StockController
│
├── domain/               # Entidades de negócio (JPA)
│   ├── Marketplace
│   ├── MarketplaceListing
│   ├── PeriodFilter
│   ├── Product
│   ├── Sale
│   ├── StockMovement
│   └── StockMovementType
│
├── dto/                  # Objetos de entrada e saída da API
│   ├── DashboardResponse
│   ├── MonthlySummaryResponse
│   ├── ProductRequest
│   └── StockEntryRequest
│
├── integration/
│   ├── mercadolivre/       # Integração completa com Mercado Livre
│   │   ├── dto/
│   │   ├── MercadoLivreAuthService
│   │   ├── MercadoLivreClient
│   │   ├── MercadoLivreController
│   │   ├── MercadoLivreImportService
│   │   ├── MercadoLivreOAuthController
│   │   └── MercadoLivreTokenResponse
│   │
│   └── shopee/             # Integração completa com Shopee
│       ├── ShopeeAuthService
│       ├── ShopeeClient
│       ├── ShopeeController
│       ├── ShopeeImportService
│       └── ShopeeTokenResponse
│
├── repository/            # Acesso ao banco de dados (Spring Data JPA)
│
└── service/                # Regras de negócio
    ├── DashboardService
    ├── FileStoreService
    ├── ImportService
    ├── ReportService
    ├── SalesService
    └── StockService
```

**Decisões de design:**
- Cada marketplace mantém seu próprio fluxo de autenticação, client e importação, evitando misturar regras específicas de Mercado Livre e Shopee.
- A importação de vendas acontece sob demanda pelo frontend, depois que o backend acorda no Render e valida os tokens necessários.
- Produtos são cadastrados manualmente e vinculados às vendas importadas por SKU.
- Entidades de domínio (`domain`) são separadas dos objetos de entrada/saída da API (`dto`), evitando expor a estrutura interna do banco diretamente nos endpoints.

**Frontend (React + Vite):**

```
src/
├── assets/
├── components/
│   └── DashboardCard.jsx
├── pages/
│   ├── Calculator.jsx
│   ├── Dashboard.jsx
│   ├── Products.jsx
│   ├── Reports.jsx
│   └── Sales.jsx
├── App.jsx
├── App.css
├── index.css
└── main.jsx
```

---

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 8 |
| Framework | Spring Boot 2 |
| Banco de dados | PostgreSQL (Neon — cloud serverless) |
| Deploy backend | Render |
| Build | Maven |
| HTTP Client | Spring WebClient |
| ORM | Spring Data JPA / Hibernate |
| Frontend | React 18 + Vite + CSS — *deploy em produção planejado* |
| Containerização | Docker (build multistage) |
| Testes manuais | Postman (validação de endpoints e contratos JSON) |
| Qualidade de código | SonarQube (análise estática) |
| Documentação de API | Swagger/OpenAPI — *planejado* |

---

## ⚙️ Como rodar localmente

### Pré-requisitos
- Java 8+
- Maven
- Conta de desenvolvedor no [Mercado Livre](https://developers.mercadolivre.com.br/) e/ou [Shopee Open Platform](https://open.shopee.com/)

### 1. Clone o repositório
```bash
git clone https://github.com/rocketsz777/ml-financeiro.git
cd ml-financeiro
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

| Variável | Descrição |
|---|---|
| `ML_CLIENT_ID` | Client ID do app no Mercado Livre |
| `ML_CLIENT_SECRET` | Client Secret do app no Mercado Livre |
| `ML_REDIRECT_URI` | URI de callback configurada no app |
| `SHOPEE_PARTNER_ID` | Partner ID da Shopee |
| `SHOPEE_PARTNER_KEY` | Partner Key da Shopee |
| `SHOPEE_REDIRECT_URI` | URI de callback configurada no app |
| `DATABASE_URL` | URL de conexão PostgreSQL (formato JDBC) |

### 3. Rode a aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`

---

## ☁️ Deploy

- Backend hospedado no **Render**, build automático a partir da branch `main`
- Banco de dados **PostgreSQL serverless via Neon**
- Variáveis de ambiente configuradas no painel do Render — nenhuma credencial versionada no código
- Frontend: deploy em produção planejado para a próxima etapa

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">
  Desenvolvido por <a href="https://github.com/rocketsz777">Alan Marques</a>
</p>
