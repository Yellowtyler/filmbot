package com.example.filmbot1;

import com.example.lib.info.movito.themoviedbapi.TmdbApi;
import com.example.lib.info.movito.themoviedbapi.TmdbMovies;
import org.springframework.stereotype.Component;

@Component
public class FilmApi {

    TmdbApi getApi()
    {
        return new TmdbApi("******************");
    }


}
