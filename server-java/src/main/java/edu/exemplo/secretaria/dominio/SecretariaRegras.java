package edu.exemplo.secretaria.dominio;

import edu.exemplo.secretaria.repo.AlunoRepository;

/** Regras de negócio da secretaria. Lógica pura: não conhece SOAP. */
public class SecretariaRegras {

    private static final double NOTA_MIN = 0.0;
    private static final double NOTA_MAX = 10.0;

    private final AlunoRepository repositorio;

    public SecretariaRegras(AlunoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Aluno consultarAluno(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new DadosInvalidosException("Matrícula não informada.");
        }
        return repositorio.buscar(matricula.trim())
                .orElseThrow(() -> new DadosInvalidosException("Matrícula inexistente: " + matricula));
    }

    public Resultado calcularSituacao(String matricula, String disciplina, double nota1, double nota2) {
        consultarAluno(matricula);
        if (disciplina == null || disciplina.isBlank()) {
            throw new DadosInvalidosException("Disciplina não informada.");
        }
        validarNota("nota1", nota1);
        validarNota("nota2", nota2);

        double media = (nota1 + nota2) / 2;
        return new Resultado(media, Situacao.daMedia(media));
    }

    private static void validarNota(String campo, double nota) {
        // NaN falha em qualquer comparação, então precisa de checagem própria
        if (Double.isNaN(nota) || nota < NOTA_MIN || nota > NOTA_MAX) {
            throw new DadosInvalidosException(campo + " deve estar entre 0 e 10 (recebido: " + nota + ").");
        }
    }
}
