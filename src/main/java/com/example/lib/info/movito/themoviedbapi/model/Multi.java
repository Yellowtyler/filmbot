package com.example.lib.info.movito.themoviedbapi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.example.lib.info.movito.themoviedbapi.model.people.PersonPeople;
import com.example.lib.info.movito.themoviedbapi.model.tv.TvSeries;


/**
 * Interface that is needed for /search/multi request
 * <p>
 * {@link com.example.lib.info.movito.themoviedbapi.model.MovieDb}, {@link com.example.lib.info.movito.themoviedbapi.model.people.Person} and
 * {@link com.example.lib.info.movito.themoviedbapi.model.tv.TvSeries} implement this interface.</p>
 * <p>Each of them returns corresponding {@link MediaType}</p>
 * @see com.example.lib.info.movito.themoviedbapi.TmdbSearch#searchMulti(String, String, Integer)
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="media_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MovieDb.class, name = "movie"),
        @JsonSubTypes.Type(value = PersonPeople.class, name = "person"),
        @JsonSubTypes.Type(value = TvSeries.class, name = "tv")})
public interface Multi {

    public enum MediaType {
        MOVIE,
        PERSON,
        TV_SERIES;
    }

    /**
     * Used to determine type Multi object without {@code instanceof()} or {@code getClass}
     */
    public MediaType getMediaType();
}

