package com.example.lib.info.movito.themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.lib.info.movito.themoviedbapi.model.core.IdElement;

import java.util.List;


public class MovieTranslations extends IdElement {

    @JsonProperty("translations")
    private List<Translation> translations;


    public List<Translation> getTranslations() {
        return translations;
    }


    public void setTranslations( List<Translation> translations ) {
        this.translations = translations;
    }
}
