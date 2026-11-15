package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.DadosInvalidosException;
import edu.exemplo.secretaria.dominio.SecretariaRegras;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

/** Service Endpoint Interface/implementação: só traduz DTO e exceção, a regra fica em {@link SecretariaRegras}. */
@WebService(
        name = "SecretariaPortType",
        serviceName = "SecretariaService",
        portName = "SecretariaPort",
        targetNamespace = SecretariaService.NAMESPACE)
public class SecretariaService {

    public static final String NAMESPACE = "http://secretaria.exemplo.edu/ws";

    private final SecretariaRegras regras;

    public SecretariaService(SecretariaRegras regras) {
        this.regras = regras;
    }

    @WebMethod
    @WebResult(name = "aluno")
    public AlunoDto consultarAluno(@WebParam(name = "matricula") String matricula) throws SecretariaFault {
        try {
            return AlunoDto.de(regras.consultarAluno(matricula));
        } catch (DadosInvalidosException e) {
            throw SecretariaFault.clienteFault(e);
        }
    }

    @WebMethod
    @WebResult(name = "resultado")
    public SituacaoDto calcularSituacao(
            @WebParam(name = "matricula") String matricula,
            @WebParam(name = "disciplina") String disciplina,
            @WebParam(name = "nota1") double nota1,
            @WebParam(name = "nota2") double nota2) throws SecretariaFault {
        try {
            return SituacaoDto.de(regras.calcularSituacao(matricula, disciplina, nota1, nota2));
        } catch (DadosInvalidosException e) {
            throw SecretariaFault.clienteFault(e);
        }
    }
}
