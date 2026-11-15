package edu.exemplo.secretaria.ws;

import edu.exemplo.secretaria.dominio.Resultado;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/** Contrato SOAP de {@code calcularSituacao}: média e situação (APROVADO, RECUPERACAO, REPROVADO). */
@XmlAccessorType(XmlAccessType.FIELD)
public class SituacaoDto {

    private double media;
    private String situacao;

    public SituacaoDto() {
        // construtor sem argumentos exigido pelo JAXB
    }

    public static SituacaoDto de(Resultado resultado) {
        SituacaoDto dto = new SituacaoDto();
        dto.media = resultado.media();
        dto.situacao = resultado.situacao().name();
        return dto;
    }

    public double getMedia() { return media; }
    public String getSituacao() { return situacao; }
}
