package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.Aluno;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/** Contrato SOAP de {@code consultarAluno}. Classe simples porque o JAXB não lida bem com record. */
@XmlAccessorType(XmlAccessType.FIELD)
public class AlunoDto {

    private String matricula;
    private String nome;
    private String curso;
    private int semestre;

    public AlunoDto() {
        // construtor sem argumentos exigido pelo JAXB
    }

    public static AlunoDto de(Aluno aluno) {
        AlunoDto dto = new AlunoDto();
        dto.matricula = aluno.matricula();
        dto.nome = aluno.nome();
        dto.curso = aluno.curso();
        dto.semestre = aluno.semestre();
        return dto;
    }

    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public String getCurso() { return curso; }
    public int getSemestre() { return semestre; }
}
