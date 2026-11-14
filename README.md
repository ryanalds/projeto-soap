# Secretaria SOAP

Serviço web SOAP de consulta acadêmica: servidor Java (JAX-WS) e cliente Python (zeep),
validado no SoapUI. Escopo e arquitetura em [`SPEC.md`](SPEC.md).

## Pré-requisitos

- JDK 17+
- Maven 3.9+
- Python 3 (cliente)
- SoapUI Open Source (testes)

## Estrutura

| Pasta | Conteúdo |
|---|---|
| `server-java/` | Servidor SOAP (Maven) |
| `client-python/` | Cliente zeep |
| `wsdl/` | Cópia do WSDL gerado |
| `soapui/` | Projeto SoapUI e documentação exportada |

## Como executar

Servidor (a partir da Ronda 3):

```
cd server-java
mvn exec:java
```

- Endpoint: <http://localhost:8080/secretaria>
- WSDL: <http://localhost:8080/secretaria?wsdl>

Testes unitários:

```
cd server-java
mvn test
```
