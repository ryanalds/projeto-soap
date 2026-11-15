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

Servidor:

```
cd server-java
mvn exec:exec
```

- Endpoint: <http://localhost:8080/secretaria>
- WSDL: <http://localhost:8080/secretaria?wsdl>

Testes unitários:

```
cd server-java
mvn test
```

Cliente Python (com o servidor no ar; `--xml` mostra os envelopes):

```
cd client-python
python3 -m venv .venv && .venv/bin/pip install -r requirements.txt
.venv/bin/python client.py [--xml]
```

Testes no SoapUI: siga [`soapui/ROTEIRO.md`](soapui/ROTEIRO.md).
Justificativa da escolha SOAP: [`docs/soap-vs-rest.md`](docs/soap-vs-rest.md).
