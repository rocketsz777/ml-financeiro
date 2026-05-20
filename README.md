# ML Financeiro

Projeto inicial em Java + Spring Boot para:
- cadastrar produtos
- registrar vendas
- calcular lucro líquido
- descontar custo do produto
- descontar taxa/frete manualmente
- gerar fechamento mensal em Excel
- receber webhook do Mercado Livre

## O que cada parte faz

### `domain/Product`
Guarda os dados do produto: SKU, ID do Mercado Livre, nome, custo e estoque.

### `domain/Sale`
Guarda os dados da venda e o lucro calculado.

### `service/FileStoreService`
Lê e grava os arquivos locais `data/produtos.csv` e `data/vendas.csv`.

### `service/SalesService`
Faz a regra de negócio:
- busca o produto
- confere estoque
- calcula lucro
- baixa estoque
- salva a venda

### `service/ReportService`
Cria o fechamento mensal e exporta um arquivo `.xlsx`.

### `integration/MercadoLivreClient`
Conecta na API do Mercado Livre com `access token`.

### `controller/*`
Expõem os endpoints para você usar pelo navegador, Postman ou Angular.

## Como rodar

### 1) Instale o Java 21
### 2) Abra uma pasta no terminal nesta pasta do projeto
### 3) Rode:
```bash
mvn spring-boot:run
```

### 4) Teste:
- `GET http://localhost:8080/`
- `GET http://localhost:8080/api/produtos`
- `POST http://localhost:8080/api/produtos`
- `POST http://localhost:8080/api/vendas`
- `GET http://localhost:8080/api/resumo/2026-05`
- `GET http://localhost:8080/api/fechamento/2026-05/excel`

## Exemplo de produto
```json
{
  "sku": "BOL001",
  "mlItemId": "MLB123",
  "name": "Bolinha Anti Estresse",
  "costPrice": 4.50,
  "stock": 100
}
```

## Exemplo de venda
```json
{
  "orderId": "123",
  "sku": "BOL001",
  "productName": "Bolinha Anti Estresse",
  "quantity": 2,
  "unitSalePrice": 19.90,
  "marketplaceFee": 3.50,
  "shippingCost": 4.00
}
```

## Onde ficam os arquivos
- `data/produtos.csv`
- `data/vendas.csv`
- `data/fechamento-YYYY-MM.xlsx`

## Próximo passo
Depois que isso estiver rodando, dá para criar o Angular para mostrar:
- vendas do mês
- lucro líquido
- estoque
- produtos mais vendidos
