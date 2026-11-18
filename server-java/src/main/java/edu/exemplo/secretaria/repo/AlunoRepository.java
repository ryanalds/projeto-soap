package edu.exemplo.secretaria.repo;

import edu.exemplo.secretaria.dominio.Aluno;

import java.util.Map;
import java.util.Optional;

//Repositório em memória com alunos por matrícula.
public class AlunoRepository {

    private final Map<String, Aluno> alunos = Map.of(
            "2024001", new Aluno("2024001", "Ana Souza", "Ciência da Computação", 3),
            "2024002", new Aluno("2024002", "Bruno Lima", "Engenharia de Software", 5),
            "2024003", new Aluno("2024003", "Carla Mendes", "Sistemas de Informação", 1));

    public Optional<Aluno> buscar(String matricula) {
        return Optional.ofNullable(alunos.get(matricula));
    }
}
