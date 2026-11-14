package edu.exemplo.secretaria.repo;

import edu.exemplo.secretaria.dominio.Aluno;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Repositório em memória, indexado por matrícula. */
public class AlunoRepository {

    private final Map<String, Aluno> alunos;

    public AlunoRepository(List<Aluno> alunos) {
        this.alunos = alunos.stream().collect(Collectors.toUnmodifiableMap(Aluno::matricula, Function.identity()));
    }

    /** Dados fixos carregados no startup. */
    public static AlunoRepository comDadosFicticios() {
        return new AlunoRepository(List.of(
                new Aluno("2024001", "Ana Souza", "Ciência da Computação", 3),
                new Aluno("2024002", "Bruno Lima", "Engenharia de Software", 5),
                new Aluno("2024003", "Carla Mendes", "Sistemas de Informação", 1)));
    }

    public Optional<Aluno> buscar(String matricula) {
        return Optional.ofNullable(alunos.get(matricula));
    }
}
