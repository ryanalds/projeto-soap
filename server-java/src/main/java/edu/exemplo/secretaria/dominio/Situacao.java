package edu.exemplo.secretaria.dominio;

public enum Situacao {
    APROVADO, RECUPERACAO, REPROVADO;

    private static final double MEDIA_APROVACAO = 7.0;
    private static final double MEDIA_RECUPERACAO = 4.0;

    public static Situacao daMedia(double media) {
        if (media >= MEDIA_APROVACAO) return APROVADO;
        if (media >= MEDIA_RECUPERACAO) return RECUPERACAO;
        return REPROVADO;
    }
}
