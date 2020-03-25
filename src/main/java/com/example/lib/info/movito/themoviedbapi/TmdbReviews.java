package com.example.lib.info.movito.themoviedbapi;

import com.example.lib.info.movito.themoviedbapi.model.Reviews;
import com.example.lib.info.movito.themoviedbapi.model.core.ResultsPage;
import com.example.lib.info.movito.themoviedbapi.tools.ApiUrl;

import static com.example.lib.info.movito.themoviedbapi.TmdbMovies.TMDB_METHOD_MOVIE;


public class TmdbReviews extends AbstractTmdbApi {

    TmdbReviews(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    public ReviewResultsPage getReviews(int movieId, String language, Integer page) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_MOVIE, movieId, "reviews");

        apiUrl.addLanguage(language);

        apiUrl.addPage(page);

        return mapJsonResult(apiUrl, ReviewResultsPage.class);
    }


    public static class ReviewResultsPage extends ResultsPage<Reviews> {

    }

}