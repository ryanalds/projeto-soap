"""Cliente SOAP da Secretaria Acadêmica. O zeep lê o WSDL e monta o XML sozinho.

Uso: python client.py          (com o servidor no ar)
     python client.py --xml    (mostra também o envelope enviado e o recebido)
"""
import sys

from lxml import etree
from zeep import Client
from zeep.exceptions import Fault
from zeep.plugins import HistoryPlugin

WSDL = "http://localhost:8080/secretaria?wsdl"
MOSTRAR_XML = "--xml" in sys.argv

historico = HistoryPlugin()
servico = Client(WSDL, plugins=[historico]).service


def chamar(titulo, operacao, **parametros):
    print(f"\n=== {titulo}")
    try:
        print(operacao(**parametros))
    except Fault as fault:
        # Erro de negócio chega como soap:Fault, que o zeep transforma em exceção
        print(f"SOAP Fault: {fault.message}")
    if MOSTRAR_XML:
        for envelope in (historico.last_sent["envelope"], historico.last_received["envelope"]):
            print(etree.tostring(envelope, pretty_print=True, encoding="unicode"))


chamar("Consulta válida", servico.consultarAluno, matricula="2024001")
chamar("Cálculo válido", servico.calcularSituacao,
       matricula="2024001", disciplina="Banco de Dados", nota1=8, nota2=9)
chamar("Matrícula inexistente", servico.consultarAluno, matricula="9999999")
chamar("Nota fora da faixa", servico.calcularSituacao,
       matricula="2024001", disciplina="Banco de Dados", nota1=11, nota2=9)
