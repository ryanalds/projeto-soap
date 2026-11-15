package edu.exemplo.secretaria;

import edu.exemplo.secretaria.dominio.SecretariaRegras;
import edu.exemplo.secretaria.repo.AlunoRepository;
import edu.exemplo.secretaria.ws.SecretariaService;
import jakarta.xml.ws.Endpoint;

public class ServerMain {

    private static final String URL = "http://localhost:8080/secretaria";

    public static void main(String[] args) {
        // repo -> regras -> service
        var service = new SecretariaService(new SecretariaRegras(AlunoRepository.comDadosFicticios()));
        Endpoint.publish(URL, service);
        System.out.println("Servidor no ar. WSDL: " + URL + "?wsdl");
    }
}
