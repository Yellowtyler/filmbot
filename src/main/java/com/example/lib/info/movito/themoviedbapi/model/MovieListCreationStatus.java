package com.example.lib.info.movito.themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.lib.info.movito.themoviedbapi.model.core.ResponseStatus;


public class MovieListCreationStatus extends ResponseStatus {

    @JsonProperty("list_id")
    private String listId;


    public String getListId() {
        return listId;
    }


    public void setListId( String listId ) {
        this.listId = listId;
    }
}
