package com.example.lib.info.movito.themoviedbapi.model.tv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.lib.info.movito.themoviedbapi.model.Credits;
import com.example.lib.info.movito.themoviedbapi.model.ExternalIds;
import com.example.lib.info.movito.themoviedbapi.model.MovieImages;
import com.example.lib.info.movito.themoviedbapi.model.Video;
import com.example.lib.info.movito.themoviedbapi.model.core.MovieKeywords;
import com.example.lib.info.movito.themoviedbapi.model.core.NamedIdElement;
import com.example.lib.info.movito.themoviedbapi.model.core.TvKeywords;
import com.example.lib.info.movito.themoviedbapi.model.keywords.Keyword;
import com.example.lib.info.movito.themoviedbapi.model.keywords.Keyword;

import java.util.List;


public class AbstractTvElement extends NamedIdElement {


    // Appendable responses for all tv elements

    @JsonProperty("credits")
    private Credits credits;

    @JsonProperty("external_ids")
    private ExternalIds externalIds;

    @JsonProperty("images")
    private MovieImages images;

    @JsonProperty("videos")
    private Video.Results videos;
    
    @JsonProperty("keywords")
    private TvKeywords keywords;

    public Credits getCredits() {
        return credits;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }


    public MovieImages getImages() {
        return images;
    }

    public void setExternalIds(ExternalIds e) {
    	externalIds = e;
    }
    
    public void setCredits(Credits c) {
    	credits = c;
    }
    
    public List<Video> getVideos() {
        return videos != null ? videos.getVideos() : null;
    }


    public void setImages( MovieImages images ) {
        this.images = images;
    }


    public void setVideos( Video.Results videos ) {
        this.videos = videos;
    }

    public List<Keyword> getKeywords() {
        return keywords != null ? keywords.getKeywords() : null;
    }

    public void setKeywords(TvKeywords keywords) {
        this.keywords = keywords;
    }
}
