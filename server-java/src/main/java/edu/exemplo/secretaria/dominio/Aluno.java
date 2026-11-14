package edu.exemplo.secretaria.dominio;

/** Aluno da secretaria. Imutável; o contrato SOAP usa um DTO próprio. */
public record Aluno(String matricula, String nome, String curso, int semestre) {
}
