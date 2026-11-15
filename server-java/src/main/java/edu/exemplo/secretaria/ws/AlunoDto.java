package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.Aluno;

/** Resposta de consultarAluno. Classe simples (e não record) porque o JAXB precisa dela para gerar o XML. */
public class AlunoDto {

    public String matricula;
    public String nome;
    public String curso;
    public int semestre;

    static AlunoDto de(Aluno aluno) {
        AlunoDto dto = new AlunoDto();
        dto.matricula = aluno.matricula();
        dto.nome = aluno.nome();
        dto.curso = aluno.curso();
        dto.semestre = aluno.semestre();
        return dto;
    }
}
