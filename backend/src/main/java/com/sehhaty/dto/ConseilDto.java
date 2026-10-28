package com.sehhaty.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sehhaty.model.Conseil;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConseilDto(Long id, String titre, String contenu, String categorie, String icone) {

    public static ConseilDto from(Conseil c) {
        return new ConseilDto(c.getId(), c.getTitre(), c.getContenu(), c.getCategorie(), c.getIcone());
    }
}
