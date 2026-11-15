package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.Resultado;

/** Resposta de calcularSituacao: média e situação (APROVADO, RECUPERACAO ou REPROVADO). */
public class SituacaoDto {

    public double media;
    public String situacao;

    static SituacaoDto de(Resultado resultado) {
        SituacaoDto dto = new SituacaoDto();
        dto.media = resultado.media();
        dto.situacao = resultado.situacao().name();
        return dto;
    }
}
