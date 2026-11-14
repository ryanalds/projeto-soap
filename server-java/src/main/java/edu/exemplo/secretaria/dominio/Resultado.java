package edu.exemplo.secretaria.dominio;

/** Resultado de {@link SecretariaRegras#calcularSituacao}: média e situação derivada. */
public record Resultado(double media, Situacao situacao) {
}
