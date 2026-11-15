"""Cliente SOAP da Secretaria Acadêmica (zeep lê o WSDL; nenhum XML é montado à mão).

Uso: python client.py [URL_DO_WSDL]   (padrão: http://localhost:8080/secretaria?wsdl)
Com --xml, imprime também o envelope bruto de cada chamada.
"""
import sys

from lxml import etree
from zeep import Client
from zeep.exceptions import Fault
from zeep.plugins import HistoryPlugin

WSDL = "http://localhost:8080/secretaria?wsdl"


def mostrar_xml(historico):
    for rotulo, msg in (("Request", historico.last_sent), ("Response", historico.last_received)):
        if msg is None:
            continue
        xml = etree.tostring(msg["envelope"], pretty_print=True, encoding="unicode")
        print(f"--- {rotulo} ---\n{xml}")


def chamar(titulo, funcao, historico, mostrar, **args):
    print(f"\n=== {titulo} {args}")
    try:
        print("OK:", funcao(**args))
    except Fault as fault:
        # No SOAP, erro de negócio chega como Fault, não como código HTTP tratado pela aplicação
        print(f"FAULT code={fault.code} message={fault.message!r}")
    if mostrar:
        mostrar_xml(historico)


def main():
    args = [a for a in sys.argv[1:] if a != "--xml"]
    mostrar = "--xml" in sys.argv
    historico = HistoryPlugin()
    servico = Client(args[0] if args else WSDL, plugins=[historico]).service

    chamar("consultarAluno válido", servico.consultarAluno, historico, mostrar, matricula="2024001")
    chamar("calcularSituacao válido", servico.calcularSituacao, historico, mostrar,
           matricula="2024001", disciplina="Banco de Dados", nota1=8, nota2=9)
    chamar("consultarAluno inexistente", servico.consultarAluno, historico, mostrar, matricula="9999999")
    chamar("calcularSituacao nota inválida", servico.calcularSituacao, historico, mostrar,
           matricula="2024001", disciplina="Banco de Dados", nota1=11, nota2=9)


if __name__ == "__main__":
    main()
