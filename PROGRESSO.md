# Progresso

Última atualização: 2026-11-14

## Como trabalhamos

- Implementação em **rondas**; ao fim de cada uma o Claude relata o que foi feito e para.
- Os commits seguem **Conventional Commits**, em português, **sem co-autoria do Claude**.
- Só commitar quando o Ryan pedir. Ao retomar, ler este arquivo, o `SPEC.md` e o `docs/arquitetura.md`.

## Ambiente

- JDK 25 (JBR), Maven 3.9.5, Python 3.15. O `pom.xml` compila com `release 17`.
- Neste shell, `ls` é um alias com opções diferentes (rejeita caminhos de arquivo). Usar `command ls` ou `find`.

## Rondas

| Ronda | Conteúdo | Status | Commit |
|---|---|---|---|
| 1 | Estrutura, `pom.xml`, README, `.gitignore` | Concluída | `f058503` |
| 1b | `SPEC.md` e `docs/arquitetura.md` | Concluída | `5ea3b4f` |
| 2 | Domínio, repositório, regras e testes JUnit | Concluída (22 testes verdes) | `d84c1e5` |
| 3 | DTOs, `SecretariaFault`, `SecretariaService`, `ServerMain`, WSDL | **Próxima** | — |
| 4 | Cliente Python (zeep) | Pendente | — |
| 5 | Projeto SoapUI, suite e documentação exportada | Pendente | — |

## Estado do código (`server-java/`)

Pronto, em `edu.exemplo.secretaria`:

- `dominio/`: `Aluno`, `Situacao`, `Resultado`, `DadosInvalidosException`, `SecretariaRegras`
- `repo/`: `AlunoRepository` (3 alunos fixos: `2024001`, `2024002`, `2024003`)
- Teste: `SecretariaRegrasTest` (22 casos, incluindo limites 3,99 / 4 / 6,99 / 7)

Ainda não existe: pacote `ws/` (só tem `.gitkeep`) e `ServerMain`. O projeto ainda não sobe servidor.

## Decisões tomadas

- Escopo: 2 operações (`consultarAluno`, `calcularSituacao`); `listarDisciplinas` foi cortada.
- Domínio imutável (`record`/`enum`); o contrato SOAP usa DTOs próprios, porque o JAXB não lida bem com `record`.
- `DadosInvalidosException` fica no **domínio**, sem SOAP. A camada `ws/` a traduz para `SecretariaFault` (`@WebFault`).
  Isso difere do `SPEC.md`, que ainda a coloca em `ws/` (seções 4 e 5). Ajustar o SPEC quando conveniente.
- Metro `jaxws-rt` 4.0.3 (`jakarta.*`). Namespace: `http://secretaria.exemplo.edu/ws`.
- Endpoint: `http://localhost:8080/secretaria` (WSDL em `?wsdl`).

## Próximos passos (Ronda 3)

1. `ws/AlunoDto` e `ws/SituacaoDto` (classes simples; conversão na borda).
2. `ws/SecretariaFault` (`@WebFault`) traduzindo `DadosInvalidosException`; faultcode `Client`.
3. `ws/SecretariaService` (`@WebService`) com `targetNamespace`, `serviceName`, `portName` e `@WebParam(name=...)` explícitos.
4. `ServerMain` montando `repo → regras → service` e chamando `Endpoint.publish`.
5. Subir o servidor de verdade (`mvn exec:java`), conferir `?wsdl` e o XML bruto de uma chamada.
6. Salvar `wsdl/secretaria.wsdl` e atualizar `docs/arquitetura.md`.

Risco principal: compatibilidade `jakarta` do Metro com o JDK 25. Se travar, testar com JDK 17 (via SDKMAN)
antes de mexer no código.

## Depois da Ronda 3

- Ronda 4: `client-python/client.py` com chamada válida e inválida, imprimindo o Fault.
- Ronda 5: projeto SoapUI a partir do WSDL (suite da seção 7 do SPEC), salvo em `soapui/`.
  O SoapUI precisa ser aberto pelo Ryan: o `.xml` do projeto é mais confiável se montado pela interface.
- Entrega: justificativa SOAP vs REST (máx. 10 linhas), prints e checklist da seção 8 do SPEC.
