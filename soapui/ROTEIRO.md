# Roteiro SoapUI

Os valores esperados abaixo foram conferidos contra o servidor real (curl e cliente Python).
O projeto `.xml` do SoapUI deve ser montado pela interface e salvo nesta pasta.

## 1. Preparar

1. Subir o servidor: `cd server-java && mvn exec:exec`.
2. SoapUI: `~/opt/SoapUI-5.8.0/bin/soapui.sh`.
3. Ver o WSDL no SoapUI: binding `SecretariaPortBinding`, operações `consultarAluno` e `calcularSituacao`.

## 2. Test suite (um TestCase por linha; step "SOAP Request")

| Caso | Operação | Entrada | Asserções |
|---|---|---|---|
| consultarAluno válido | `consultarAluno` | `matricula=2024001` | Not SOAP Fault; XPath `//aluno/nome` = `Ana Souza` |
| consultarAluno inexistente | `consultarAluno` | `matricula=9999999` | SOAP Fault; Contains `Matrícula inexistente` |
| consultarAluno em branco | `consultarAluno` | `matricula=` (vazio) | SOAP Fault; Contains `Matrícula não informada` |
| situação aprovado | `calcularSituacao` | `2024001`, `Banco de Dados`, `8`, `9` | XPath `//resultado/media` = `8.5`; `//resultado/situacao` = `APROVADO` |
| situação recuperação | `calcularSituacao` | `2024001`, `Banco de Dados`, `5`, `5` | `media` = `5.0`; `situacao` = `RECUPERACAO` |
| situação reprovado | `calcularSituacao` | `2024001`, `Banco de Dados`, `2`, `3` | `media` = `2.5`; `situacao` = `REPROVADO` |
| nota fora da faixa | `calcularSituacao` | `2024001`, `Banco de Dados`, `11`, `9` | SOAP Fault; Contains `nota1 deve estar entre 0 e 10` |

Corpo de exemplo (SoapUI gera o Envelope; troque os `?`):

```xml
<ws:calcularSituacao xmlns:ws="http://secretaria.exemplo.edu/ws">
  <matricula>2024001</matricula>
  <disciplina>Banco de Dados</disciplina>
  <nota1>8</nota1>
  <nota2>9</nota2>
</ws:calcularSituacao>
```

## 3. Detalhes que costumam dar erro

- Só o elemento da operação e o de resposta têm namespace; `matricula`, `aluno`, `nome`, `resultado`, `media`...
  são **sem prefixo**. XPath certo: `//aluno/nome`. `//ws:aluno/ws:nome` não acha nada.
- Os Faults voltam com **HTTP 500**. A asserção **SOAP Fault** os aceita; **Not SOAP Fault** é só para os casos válidos.
- Os Faults de negócio trazem o elemento `SecretariaFault` no `detail`; dá para checar com XPath
  `//*[local-name()='SecretariaFault']/message` (o `faultcode` é `S:Server`, padrão do JAX-WS).
- Evite `?` na asserção Contains com acentos mal digitados: copie o texto exatamente.

## 4. Conformidade e documentação

- Em cada step válido, adicionar **Schema Compliance** (usa o `.xsd` do WSDL).
- No Interface (binding): botão direito > **Check WSDL Compliance / WS-I Compliance**, se disponível na sua versão.
- Rodar a suite (**Run TestSuite**), conferir tudo verde e tirar print.
- Salvar o projeto em `soapui/secretaria-soapui-project.xml` (**File > Save Project As**).
- Documentação: botão direito no projeto > **Launch HTML Report** / **Generate Documentation**;
  exportar o resultado para `soapui/docs/`.
