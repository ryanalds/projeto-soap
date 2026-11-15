# Arquitetura: para que serve cada parte

Guia de leitura do projeto. Mostra o papel de cada componente e o caminho de uma requisição.
O status indica em qual ronda de implementação cada parte entra.

## Visão geral

```
┌───────────────┐                         ┌──────────────────────────────────────────┐
│ Cliente Python│                         │            Servidor Java (JAX-WS)        │
│    (zeep)     │──┐                      │                                          │
└───────────────┘  │   HTTP + SOAP/XML    │  SecretariaService ─▶ SecretariaRegras   │
                   ├─────────────────────▶│     (ws/)                (dominio/)      │
┌───────────────┐  │                      │         │                    │           │
│    SoapUI     │──┘                      │     DTOs/Fault               ▼           │
│ (testes+docs) │                         │                        AlunoRepository   │
└───────────────┘                         │                            (repo/)       │
        ▲                                 └──────────────────────────────────────────┘
        └─────────── lê o contrato em http://localhost:8080/secretaria?wsdl
```

## Componentes

### Servidor (`server-java/`)

| Parte | Camada | Para que serve | Status |
|---|---|---|---|
| `pom.xml` | Build | Declara Java 17, Metro `jaxws-rt` (JAX-WS não vem no JDK 11+), JUnit 5 e o `exec:exec` que sobe o servidor | Ronda 1 |
| `Aluno` | Domínio | Registro imutável (`record`) com matrícula, nome, curso e semestre | Ronda 2 |
| `Situacao` | Domínio | Enum `APROVADO` / `RECUPERACAO` / `REPROVADO` | Ronda 2 |
| `AlunoRepository` | Repositório | Guarda os alunos fixos em um `Map` em memória e busca por matrícula | Ronda 2 |
| `Resultado` | Domínio | Registro com a média e a situação devolvidos por `calcularSituacao` | Ronda 2 |
| `DadosInvalidosException` | Domínio | Exceção de entrada inválida ou entidade inexistente. Não tem nada de SOAP | Ronda 2 |
| `SecretariaRegras` | Domínio | Lógica pura: valida matrícula e notas, calcula média e situação. Não conhece SOAP, por isso é testável com JUnit simples | Ronda 2 |
| `SecretariaFault` | Contrato (ws) | Exceção `@WebFault`: declara o fault no WSDL e o Metro a converte em `soap:Fault` com a mensagem legível | Ronda 3 |
| `AlunoDto` / `SituacaoDto` | Contrato (ws) | Classes simples que formam o XML do WSDL. Isolam o contrato do modelo interno (o JAXB não lida bem com `record`) | Ronda 3 |
| `SecretariaService` | Contrato (ws) | Classe `@WebService` com as operações `consultarAluno` e `calcularSituacao`. Só recebe, delega às regras e converte para DTO | Ronda 3 |
| `ServerMain` | Bootstrap | Monta o grafo (`repo → regras → service`) por construtor e chama `Endpoint.publish` | Ronda 3 |

### Fora do servidor

| Parte | Para que serve | Status |
|---|---|---|
| `wsdl/secretaria.wsdl` + `secretaria.xsd` | Cópia versionada do contrato gerado; o `schemaLocation` aponta para o `.xsd` local | Ronda 3 |
| `client-python/client.py` | Consome o serviço pelo WSDL (zeep): uma chamada válida e uma inválida, imprimindo o Fault | Ronda 4 |
| `soapui/` | Projeto SoapUI com a suite de testes e a documentação exportada | Ronda 5 |
| `SPEC.md` | Escopo, decisões e critérios de aceite | Ronda 1 |

## Fluxo de uma requisição

Exemplo: `consultarAluno` com a matrícula `2024001`.

```
1. Cliente (zeep / SoapUI) lê o WSDL em /secretaria?wsdl
2. Monta o Envelope SOAP  ──▶  POST http://localhost:8080/secretaria
3. Metro (JAX-WS) desserializa o XML e chama SecretariaService.consultarAluno("2024001")
4. SecretariaService delega para SecretariaRegras.consultarAluno(...)
5. SecretariaRegras valida a matrícula e pede ao AlunoRepository.buscar(...)
6. AlunoRepository devolve o Aluno do Map
7. SecretariaService converte Aluno ──▶ AlunoDto
8. Metro serializa o DTO em um Envelope de resposta  ──▶  cliente recebe nome, curso e semestre
```

### Fluxo de erro (SOAP Fault)

```
matrícula "" ou "9999999"
   └─▶ SecretariaRegras lança DadosInvalidosException
        └─▶ SecretariaService lança SecretariaFault (@WebFault) com a mesma mensagem
             └─▶ Metro converte em <soap:Fault> (HTTP 500, faultcode "Server")
             └─▶ cliente recebe faultstring legível + detail <SecretariaFault>

exceção inesperada (bug)
   └─▶ Metro devolve <soap:Fault> sem o detail <SecretariaFault>
```

### Fluxo de `calcularSituacao`

```
matricula, disciplina, nota1, nota2
   ├─ matrícula inexistente ──▶ Fault
   ├─ nota fora de 0–10     ──▶ Fault
   └─ media = (nota1 + nota2) / 2
        ├─ media >= 7  ──▶ APROVADO
        ├─ media >= 4  ──▶ RECUPERACAO
        └─ senão       ──▶ REPROVADO
```

## Por que essa divisão

- **SEI fina:** quem fala SOAP não decide regra de negócio.
- **Regras puras:** testáveis sem subir servidor, porque não dependem de SOAP.
- **DTOs na borda:** o WSDL continua estável mesmo que o modelo interno mude.
- **Injeção por construtor:** sem framework, o `ServerMain` mostra todas as dependências em um só lugar.
