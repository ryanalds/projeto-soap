package edu.exemplo.secretaria.dominio;

/**
 * Entrada inválida ou entidade inexistente. Pertence ao domínio (sem nada de SOAP);
 * a camada ws a traduz para um soap:Fault.
 */
public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
