package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.DadosInvalidosException;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.WebFault;
import jakarta.xml.ws.soap.SOAPFaultException;

import javax.xml.namespace.QName;

/**
 * Erro de negócio declarado no WSDL (elemento {@code SecretariaFault} no {@code detail}).
 * <p>
 * O JAX-WS sempre emite {@code faultcode=Server} para exceções declaradas. Como erro de entrada é
 * culpa do cliente, a SEI lança o {@link SOAPFaultException} criado por {@link #clienteFault}, que
 * mantém o mesmo {@code detail} mas com {@code faultcode=Client}. A classe continua declarada em
 * {@code throws} para o fault entrar no contrato.
 */
@WebFault(name = "SecretariaFault", targetNamespace = SecretariaService.NAMESPACE)
public class SecretariaFault extends Exception {

    public SecretariaFault(String mensagem) {
        super(mensagem);
    }

    public static SOAPFaultException clienteFault(DadosInvalidosException e) {
        try {
            SOAPFactory fabrica = SOAPFactory.newInstance();
            SOAPFault fault = fabrica.createFault(
                    e.getMessage(), new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client"));
            fault.addDetail()
                    .addDetailEntry(new QName(SecretariaService.NAMESPACE, "SecretariaFault"))
                    .addChildElement("message")
                    .setTextContent(e.getMessage());
            return new SOAPFaultException(fault);
        } catch (SOAPException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
