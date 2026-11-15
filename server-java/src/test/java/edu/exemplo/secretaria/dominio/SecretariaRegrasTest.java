package edu.exemplo.secretaria.dominio;

import edu.exemplo.secretaria.repo.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecretariaRegrasTest {

    private static final String MATRICULA = "2024001";

    private final SecretariaRegras regras = new SecretariaRegras(new AlunoRepository());

    @Test
    void consultaAlunoExistente() {
        Aluno aluno = regras.consultarAluno(MATRICULA);

        assertEquals("Ana Souza", aluno.nome());
        assertEquals(3, aluno.semestre());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "9999999"})
    void consultaComMatriculaInvalidaLancaExcecao(String matricula) {
        assertThrows(DadosInvalidosException.class, () -> regras.consultarAluno(matricula));
    }

    @ParameterizedTest(name = "{0} e {1} -> media {2}, {3}")
    @CsvSource({
            "8.0,  9.0,  8.5,  APROVADO",
            "5.0,  5.0,  5.0,  RECUPERACAO",
            "2.0,  3.0,  2.5,  REPROVADO",
            // limites das faixas
            "7.0,  7.0,  7.0,  APROVADO",
            "7.0,  6.98, 6.99, RECUPERACAO",
            "4.0,  4.0,  4.0,  RECUPERACAO",
            "4.0,  3.98, 3.99, REPROVADO",
            "0.0,  0.0,  0.0,  REPROVADO",
            "10.0, 10.0, 10.0, APROVADO"
    })
    void calculaMediaESituacao(double nota1, double nota2, double mediaEsperada, Situacao esperada) {
        Resultado r = regras.calcularSituacao(MATRICULA, "Redes", nota1, nota2);

        assertEquals(mediaEsperada, r.media(), 1e-9);
        assertEquals(esperada, r.situacao());
    }

    @ParameterizedTest
    @CsvSource({"-0.1, 5", "5, 10.1", "11, 5", "5, -1", "NaN, 5"})
    void notaForaDaFaixaLancaExcecao(double nota1, double nota2) {
        assertThrows(DadosInvalidosException.class, () -> regras.calcularSituacao(MATRICULA, "Redes", nota1, nota2));
    }

    @Test
    void calculoComMatriculaInexistenteLancaExcecao() {
        assertThrows(DadosInvalidosException.class, () -> regras.calcularSituacao("9999999", "Redes", 5, 5));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void calculoSemDisciplinaLancaExcecao(String disciplina) {
        assertThrows(DadosInvalidosException.class, () -> regras.calcularSituacao(MATRICULA, disciplina, 5, 5));
    }
}
