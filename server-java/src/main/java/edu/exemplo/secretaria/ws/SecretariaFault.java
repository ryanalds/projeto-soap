package edu.exemplo.secretaria.ws;

import jakarta.xml.ws.WebFault;

/** Erro de negócio declarado no WSDL. O JAX-WS o converte em soap:Fault com a mensagem em faultstring. */
@WebFault(name = "SecretariaFault", targetNamespace = SecretariaService.NAMESPACE)
public class SecretariaFault extends Exception {

    public SecretariaFault(String mensagem) {
        super(mensagem);
    }
}
