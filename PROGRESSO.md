# Progresso

Última atualização: 2026-11-15 (ronda 5, parcial)

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
| 3 | DTOs, `SecretariaFault`, `SecretariaService`, `ServerMain`, WSDL | Concluída | `c7e3713` |
| 4 | Cliente Python (zeep) | Concluída | `5c4955a` |
| 5 | Projeto SoapUI, suite e documentação exportada | **Parcial**: roteiro pronto (`soapui/ROTEIRO.md`); projeto depende do Ryan | — |

## Estado do código (`server-java/`)

Em `edu.exemplo.secretaria`:

- `dominio/`: `Aluno`, `Situacao`, `Resultado`, `DadosInvalidosException`, `SecretariaRegras`
- `repo/`: `AlunoRepository` (3 alunos fixos: `2024001`, `2024002`, `2024003`)
- `ws/`: `AlunoDto`, `SituacaoDto`, `SecretariaFault`, `SecretariaService`
- `ServerMain`
- Teste: `SecretariaRegrasTest` (22 casos, verdes)

Subir o servidor: `cd server-java && mvn exec:exec` (WSDL em `http://localhost:8080/secretaria?wsdl`).
`wsdl/secretaria.wsdl` + `wsdl/secretaria.xsd` são cópias do contrato gerado.

## Verificado na Ronda 3 (servidor real, XML bruto via curl)

- WSDL document/literal wrapped, namespace e nomes conforme o SPEC; funciona com JDK 25 (sem precisar do JDK 17).
- `consultarAluno` 2024001 → dados; matrícula vazia/inexistente → Fault.
- `calcularSituacao`: 8 e 9 → 8.5 APROVADO; 5 e 5 → RECUPERACAO; 2 e 3 → REPROVADO; `nota1=11` → Fault.
- Fault: HTTP 500, `faultcode=S:Server`, `faultstring` legível e `detail/SecretariaFault/message`.

## Armadilhas descobertas

- **`mvn exec:java` quebra Faults**: o classloader dele impede o JAXB de ser achado ao montar o fault (HTTP 500 vazio,
  `ClassNotFoundException: org.glassfish.jaxb.runtime.v2.ContextFactory`). Por isso o pom usa `exec:exec` (JVM separada).
- **Faultcode**: exceção `@WebFault` declarada sai sempre como `Server` no Metro. Decisão (2026-11-15): aceitar `Server`
  para manter o código simples; montar o Fault à mão só para trocar o código para `Client` não compensava.
- No zsh, aspas na URL com `?wsdl` e cuidado com `pgrep -f`/`pkill -f`: casam com o próprio shell do comando.

## Ronda 4 (cliente Python)

- `client-python/client.py` + `requirements.txt` (`zeep>=4.2`; testado com zeep 4.3.3, Python 3.15).
- Rodar: `python3 -m venv .venv && .venv/bin/pip install -r requirements.txt && .venv/bin/python client.py [--xml]`
  (com o servidor no ar). `--xml` imprime envelope de requisição e resposta.
- Verificado de verdade: consulta válida, cálculo 8 e 9 → 8.5 APROVADO, e dois Faults (matrícula
  inexistente e `nota1=11`) capturados como `zeep.exceptions.Fault`.

## Ronda 5 (SoapUI) — parcial

- SoapUI Open Source 5.8.0 instalado em `~/opt/SoapUI-5.8.0` (abrir com `bin/soapui.sh`).
- Decisão (2026-11-15): o projeto é montado pelo Ryan na interface; o Claude só confere o `.xml` salvo e roda o
  `testrunner.sh` (gerar o projeto por script foi bloqueado pelo modo de permissão).
- `soapui/ROTEIRO.md`: passo a passo, os 7 casos da suite com entradas, asserções e valores esperados (conferidos
  no servidor real), e as armadilhas (filhos sem namespace, Faults com HTTP 500).
- Falta o Ryan: montar a suite, rodar, salvar `soapui/secretaria-soapui-project.xml`, exportar documentação e prints.

## Simplificação (2026-11-15, antes do SoapUI)

Pedido do Ryan: código de baixa complexidade e fácil manutenção.
- `SecretariaFault`: `@WebFault` padrão (sem `SOAPFactory`/`SOAPFaultException`). Faultcode passou a `Server`.
- DTOs: campos públicos, sem getters nem anotações JAXB.
- `AlunoRepository`: `Map.of` direto, construtor sem argumentos (sumiu `comDadosFicticios`).
- `client.py`: script linear; sem argumento de URL, só `--xml`.
- O WSDL/XSD gerados não mudaram (conferido por diff); testes e cliente rodados de novo.

## Entrega

- Feito: `docs/soap-vs-rest.md` (7 linhas), README atualizado e checklist do SPEC marcado, exceto a suite SoapUI.
- Falta (Ryan): suite SoapUI verde + documentação exportada + prints (ver `soapui/ROTEIRO.md`).
