package com.backbase.movieapp.controller;

import com.backbase.movieapp.model.Movie;
import com.backbase.movieapp.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Value("${api.token}")
  private String apiToken;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public MovieService movieService() {
      return Mockito.mock(MovieService.class);
    }
  }
  @Autowired
  private MovieService movieService;

  private final String TITLE = "Inception";

  @Test
  @DisplayName("GET /api/movies/{title} - should return movie")
  void shouldReturnMovieByTitle() throws Exception {
    Movie movie = new Movie();
    movie.setTitle(TITLE);
    movie.setWonOscar(true);

    Mockito.when(movieService.getMovieByTitle(TITLE)).thenReturn(movie);

    mockMvc.perform(get("/api/movies/Inception")
            .header("x-api-token", apiToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(TITLE))
            .andExpect(jsonPath("$.wonOscar").value(true));
  }

  @Test
  @DisplayName("GET /api/movies/{title}/best-picture - should return Oscar message")
  void shouldReturnBestPictureMessage() throws Exception {
    Movie movie = new Movie();
    movie.setTitle("The Godfather");
    movie.setWonOscar(true);

    Mockito.when(movieService.getMovieByTitle("The%20Godfather")).thenReturn(movie);

    mockMvc.perform(get("/api/movies/The%20Godfather/best-picture")
            .header("x-api-token", apiToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.won").value(true))
            .andExpect(jsonPath("$.message", containsString("won the Best Picture Oscar")));
  }

  @Test
  @DisplayName("POST /api/movies/{title}/rate - valid rating")
  void shouldRateMovieSuccessfully() throws Exception {
    mockMvc.perform(post("/api/movies/"+ TITLE + "/rate")
                    .param("rating", "8")
            .header("x-api-token", apiToken))
            .andExpect(status().isCreated());

    Mockito.verify(movieService).rateMovie(TITLE, 8.0);
  }

  @Test
  @DisplayName("POST /api/movies/{title}/rate - invalid rating")
  void shouldReturn400WhenRatingIsInvalid() throws Exception {
    mockMvc.perform(post("/api/movies/Inception/rate")
                    .param("rating", "11")
            .header("x-api-token", apiToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(containsString("must be less than or equal to 10")));

  }

  @Test
  @DisplayName("GET /api/movies/top-rated - should return list of top-rated movies")
  void shouldReturnTopRatedMovies() throws Exception {
    Movie movie1 = new Movie();
    movie1.setTitle(TITLE);

    Movie movie2 = new Movie();
    movie2.setTitle("The Dark Knight");

    Mockito.when(movieService.getTopRatedMovies()).thenReturn(List.of(movie1, movie2));

    mockMvc.perform(get("/api/movies/top-rated")
            .header("x-api-token", apiToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value(TITLE))
            .andExpect(jsonPath("$[1].title").value("The Dark Knight"));
  }
}

