package com.backbase.movieapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class OmdbResponse {
  private String Title;

  private String Year;

  private String Rated;

  private String Released;

  private String Runtime;

  private String Genre;

  private String Director;

  private String Writer;

  private String Actors;

  private String Plot;

  private String Language;

  private String Country;

  private String Awards;

  private String Poster;

  private List<OmdbRating> Ratings;

  private String Metascore;

  private String imdbRating;

  private String imdbVotes;

  private String imdbID;

  private String Type;

  private String DVD;

  private String BoxOffice;

  private String Production;

  private String Website;

  private String Response;
  private String Error;
}
