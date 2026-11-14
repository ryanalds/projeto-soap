# Serviço SOAP — Secretaria Acadêmica

> Atividade acadêmica: serviço SOAP (servidor Java + cliente Python), validado no SoapUI.
> Fonte de verdade para a implementação.

## 1. Escopo

Serviço pequeno, com contrato WSDL formal, consumido por outra linguagem e testado no SoapUI.
Qualidade acima de volume: poucas classes, cada uma com uma responsabilidade clara.

**Fora do escopo:** REST/JSON, banco de dados, Spring, GUI, autenticação, WS-Security, deploy.

## 2. Stack

| Item | Escolha |
|---|---|
| Servidor | Java 17 + Jakarta XML Web Services (Metro `jaxws-rt`), `Endpoint.publish` |
| Cliente | Python 3 + `zeep` (lê o WSDL pela URL, sem montar XML à mão) |
| Contrato | Code-first com anotações explícitas; WSDL document/literal wrapped, cópia em `wsdl/` |
| Dados | `Map` em memória, 3 alunos fixos |
| Build/Teste | Maven + JUnit 5; SoapUI para testes de integração |

- Namespace: `http://secretaria.exemplo.edu/ws`
- Endpoint: `http://localhost:8080/secretaria` (WSDL em `?wsdl`)

## 3. Operações

| Operação | Entrada | Saída | SOAP Fault (`Client`) |
|---|---|---|---|
| `consultarAluno` | `matricula` | `nome`, `curso`, `semestre` | matrícula em branco ou inexistente |
| `calcularSituacao` | `matricula`, `disciplina`, `nota1`, `nota2` | `media`, `situacao` | nota fora de 0–10, matrícula inválida |

Regra: `media = (nota1 + nota2) / 2`. `>= 7` APROVADO, `>= 4` RECUPERACAO, senão REPROVADO.

Erros de negócio: `DadosInvalidosException` (`@WebFault`) vira `soap:Fault` com mensagem legível.
Exceção inesperada vira Fault `Server`.

## 4. Arquitetura

```
Cliente Python (zeep) ─┐
                       ├─ SOAP/HTTP ─▶ SecretariaService (SEI)  ─▶ SecretariaRegras ─▶ AlunoRepository
SoapUI ────────────────┘                  só valida e delega      lógica pura          Map em memória
```

Princípios de design:
- **Camadas finas e desacopladas:** a SEI não contém regra; a regra não conhece SOAP (testável com JUnit puro).
- **Domínio imutável:** `Aluno` e `Situacao` (enum) são `record`/`enum`. O contrato SOAP expõe apenas DTOs
  simples (`AlunoDto`, `SituacaoDto`), convertidos na borda, para o WSDL não depender do modelo interno.
- **Injeção por construtor:** `ServerMain` monta o grafo (`repo → regras → service`) e publica.
- **Contrato explícito:** `targetNamespace`, `serviceName`, `portName` e `@WebParam(name=...)` declarados.

## 5. Estrutura

```
projeto-soap/
├── SPEC.md
├── README.md
├── server-java/
│   ├── pom.xml
│   └── src/{main,test}/java/edu/exemplo/secretaria/
│       ├── ServerMain.java
│       ├── ws/        SecretariaService.java, AlunoDto.java, SituacaoDto.java, DadosInvalidosException.java
│       ├── dominio/   Aluno.java, Situacao.java, SecretariaRegras.java
│       └── repo/      AlunoRepository.java
├── client-python/     client.py, requirements.txt
├── wsdl/              secretaria.wsdl
└── soapui/            projeto .xml + docs exportada
```

## 6. Plano (commits pequenos)

1. `chore`: estrutura, `pom.xml`, README.
2. `feat(server)`: domínio, repositório e regras + testes JUnit.
3. `feat(server)`: SEI, DTOs, `@WebFault`, `ServerMain`; WSDL acessível; salvar `wsdl/secretaria.wsdl`.
4. `feat(client)`: `client.py` com chamada válida e inválida (imprime o Fault).
5. `test(soapui)`: projeto, test suite e documentação exportada.

## 7. Testes

**JUnit:** média e limites da situação (3,99 / 4 / 6,99 / 7), notas inválidas, matrícula inexistente.

**SoapUI** (projeto criado a partir do WSDL):

| Caso | Entrada | Esperado |
|---|---|---|
| consultarAluno válido | `2024001` | dados do aluno (XPath `nome`) |
| consultarAluno inexistente / em branco | `9999999` / `""` | SOAP Fault |
| calcularSituacao | 8 e 9 / 5 e 5 / 2 e 3 | 8.5 APROVADO / RECUPERACAO / REPROVADO |
| nota fora da faixa | `nota1 = 11` | SOAP Fault |
| conformidade | — | WS-I Basic Profile / Schema Compliance, se disponível |

## 8. Critérios de aceite

- [ ] Servidor Java no ar, WSDL acessível por URL, ≥ 2 operações
- [ ] Cliente Python consumindo; chamada válida e inválida (Fault) demonstradas
- [ ] Envelope/Body/namespaces conferidos no XML bruto
- [ ] Suite SoapUI verde + documentação exportada
- [ ] Commits mostrando a evolução

## 9. Riscos técnicos

- JAX-WS não vem no JDK 11+: incluir `jakarta.xml.ws-api` + `jaxws-rt` e usar pacotes `jakarta.*` de forma consistente.
- JAXB não lida bem com `record`: por isso os DTOs do contrato são classes simples.
- Conferir o WSDL gerado (namespace e nomes) antes de escrever cliente e testes.

## 10. Instruções para o Claude Code

- Seguir esta SPEC; perguntar antes de mudar tema, operações ou linguagens.
- Código pequeno; comentários só onde explicam SOAP.
- Rodar servidor e cliente de verdade antes de dizer que funciona.
