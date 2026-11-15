# Por que SOAP neste serviço

1. O WSDL é um contrato formal e legível por máquina: o zeep e o SoapUI geraram as chamadas só a partir dele, sem documentação à parte.
2. Os tipos vêm tipados no XSD (`nota1` é `xs:double`, `semestre` é `xs:int`); o contrato dispensa validação manual de formato no cliente.
3. Os erros de negócio são um `soap:Fault` padronizado (`faultcode`, `faultstring`, `detail`), declarado no WSDL; em REST cada API inventa seu formato de erro.
4. Cliente Java e cliente Python consomem o mesmo contrato sem acordo extra, o que atende bem a integração entre linguagens.
5. Há ferramentas prontas (SoapUI) para testar contra o WSDL, com verificação de conformidade ao schema.
6. Custo: o XML é mais verboso que JSON, o acoplamento ao contrato é maior e o ecossistema de ferramentas é menos ativo.
7. Para um serviço público, simples e orientado a recursos, REST seria a escolha natural; SOAP se justifica aqui pela exigência de contrato formal.
