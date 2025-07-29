package com.backbase.movieapp.service;

import com.backbase.movieapp.dto.OmdbRating;
import com.backbase.movieapp.dto.OmdbResponse;
import com.backbase.movieapp.externalservice.OmdbApi;
import com.backbase.movieapp.model.Movie;
import com.backbase.movieapp.model.Rating;
import com.backbase.movieapp.repository.MovieRepository;
import com.backbase.movieapp.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

  @Mock
  private MovieRepository movieRepository;

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private OmdbApi api;

  @InjectMocks
  private MovieService movieService;

  private final OmdbRating OMDB_RATING = new OmdbRating();
  private final String TITLE="Gladiator";

  @Test
  void getMovieByTitle_existingMovieWithRatings_returnsMovie() {
    Movie movie = new Movie();
    movie.setTitle("Inception");
    movie.setRatings(List.of(new Rating()));
    movie.setBoxOffice(1000000L);

    when(movieRepository.findByTitleIgnoreCase("Inception")).thenReturn(Optional.of(movie));

    Movie result = movieService.getMovieByTitle("Inception");

    assertNotNull(result);
    assertEquals("Inception", result.getTitle());
    verify(api, never()).fetchMovieData(anyString());
  }

  @Test
  void getMovieByTitle_existingMovieMissingRatings_fetchesAndUpdates() {
    Movie movie = new Movie();
    movie.setTitle(TITLE);
    movie.setRatings(new ArrayList<>());
    movie.setBoxOffice(null);

    OmdbResponse response = new OmdbResponse();

    response.setRatings(List.of(OMDB_RATING));
    response.setBoxOffice("$292,576,195");
    response.setAwards("Won 4 Oscars.");
    response.setTitle(TITLE);

    when(movieRepository.findByTitleIgnoreCase(TITLE)).thenReturn(Optional.of(movie));
    when(api.fetchMovieData(TITLE)).thenReturn(response);

    Movie result = movieService.getMovieByTitle(TITLE);

    assertEquals(292576195L, result.getBoxOffice());
    assertTrue(result.isWonOscar());
    verify(ratingRepository).saveAll(anyList());
    verify(movieRepository, atLeastOnce()).save(movie);
  }

  @Test
  void getMovieByTitle_newMovie_savesAndReturns() {
    OmdbResponse response = new OmdbResponse();
    response.setTitle(TITLE);
    response.setBoxOffice("$123,456");
    response.setAwards("Won 1 Oscar.");
    response.setRatings(List.of(OMDB_RATING));

    Movie savedMovie = new Movie();
    savedMovie.setTitle(TITLE);
    savedMovie.setRatings(new ArrayList<>());

    when(movieRepository.findByTitleIgnoreCase(TITLE)).thenReturn(Optional.empty());
    when(api.fetchMovieData(TITLE)).thenReturn(response);
    when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

    Movie result = movieService.getMovieByTitle(TITLE);

    assertEquals(TITLE, result.getTitle());
    verify(movieRepository).save(any(Movie.class));
    verify(ratingRepository).saveAll(anyList());
  }

  @Test
  void rateMovie_addsNewUserRating_success() {
    Movie movie = new Movie();
    movie.setTitle(TITLE);
    Rating rate = new Rating();
    rate.setValue("5");
    rate.setSource("IMDB");
    rate.setMovie(movie);
    movie.setRatings(new ArrayList<>(List.of(rate)));
    movie.setBoxOffice(1000000L);

    when(movieRepository.findByTitleIgnoreCase(TITLE)).thenReturn(Optional.of(movie));
    when(movieRepository.save(any(Movie.class))).thenReturn(movie);

    movieService.rateMovie(TITLE, 9.0);

    assertEquals(2, movie.getRatings().size());
    assertEquals("UserRating", movie.getRatings().get(1).getSource());
    assertEquals("9.0", movie.getRatings().get(1).getValue());
    verify(movieRepository).save(movie);
  }
  @Test
  void rateMovie_updateExistingUserRating_success() {
    Movie movie = new Movie();
    movie.setTitle(TITLE);
    Rating rate = new Rating();
    rate.setValue("5.0");
    rate.setSource("UserRating");
    rate.setMovie(movie);
    movie.setRatings(new ArrayList<>(List.of(rate)));
    movie.setBoxOffice(1000000L);

    when(movieRepository.findByTitleIgnoreCase(TITLE)).thenReturn(Optional.of(movie));
    when(movieRepository.save(any(Movie.class))).thenReturn(movie);

    movieService.rateMovie(TITLE, 9.0);

    assertEquals(1, movie.getRatings().size());
    assertEquals("UserRating", movie.getRatings().get(0).getSource());
    assertEquals("9.0", movie.getRatings().get(0).getValue());
    verify(movieRepository).save(movie);
  }
  @Test
  void rateMovie_newMovieUpdateUserRating_success() {
    Movie movie = new Movie();
    movie.setTitle(TITLE);

    when(movieRepository.findByTitleIgnoreCase(TITLE)).thenReturn(Optional.of(movie));
    when(movieRepository.save(any(Movie.class))).thenReturn(movie);

    OmdbResponse response = new OmdbResponse();
    response.setTitle(TITLE);
    response.setBoxOffice("$456,789");
    response.setRatings(Collections.emptyList());
    response.setAwards("");

    when(api.fetchMovieData(TITLE)).thenReturn(response);

    movieService.rateMovie(TITLE, 9.0);

    assertEquals(1, movie.getRatings().size());
    assertEquals("UserRating", movie.getRatings().get(0).getSource());
    assertEquals("9.0", movie.getRatings().get(0).getValue());
    verify(movieRepository, times(2)).save(movie);
  }

  @Test
  void getTopRatedMovies_returnsTop10() {
    List<Movie> topMovies = List.of(new Movie(), new Movie());

    when(movieRepository.findTopRatedMoviesOrderedByBoxOffice(PageRequest.of(0, 10)))
            .thenReturn(topMovies);

    List<Movie> result = movieService.getTopRatedMovies();

    assertEquals(2, result.size());
    verify(movieRepository).findTopRatedMoviesOrderedByBoxOffice(PageRequest.of(0, 10));
  }

  @Test
  void parseBoxOffice_handlesInvalidData() throws Exception {
    // Using reflection to test private method (optional, or expose method in test utils)
    var method = MovieService.class.getDeclaredMethod("parseBoxOffice", String.class);
    method.setAccessible(true);
    long result = (long) method.invoke(movieService, "invalid-data");
    assertEquals(0, result);
  }

  @Test
  void hasWonOscar_detectsOscarWins() throws Exception {
    var method = MovieService.class.getDeclaredMethod("hasWonOscar", String.class);
    method.setAccessible(true);

    assertTrue((boolean) method.invoke(movieService, "Won 2 Oscars."));
    assertFalse((boolean) method.invoke(movieService, "Nominated for 3 BAFTAs."));
  }
}

