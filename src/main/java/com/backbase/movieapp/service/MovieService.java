package com.backbase.movieapp.service;

import com.backbase.movieapp.dto.OmdbRating;
import com.backbase.movieapp.dto.OmdbResponse;
import com.backbase.movieapp.externalservice.OmdbApi;
import com.backbase.movieapp.model.Movie;
import com.backbase.movieapp.model.Rating;
import com.backbase.movieapp.repository.MovieRepository;
import com.backbase.movieapp.repository.RatingRepository;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {
  private final MovieRepository movieRepository;
  private final RatingRepository ratingRepository;
  private final OmdbApi api;


  private static final Pattern OSCAR_PATTERN = Pattern.compile("Won (\\d+) Oscar(s)?", Pattern.CASE_INSENSITIVE);

  public Movie getMovieByTitle(String title) {
    return movieRepository.findByTitleIgnoreCase(title)
            .map(movie -> {
              if (movie.getRatings().isEmpty() || movie.getBoxOffice() == null) {
                updateMovieWithOmdbData(movie, api.fetchMovieData(title));
              }
              return movie;
            })
            .orElseGet(() -> saveNewMovie(api.fetchMovieData(title)));
  }

  public void rateMovie(String title, double rating) {
    Movie movie = getMovieByTitle(title);
    List<Rating> ratings = movie.getRatings();

    Rating userRating = ratings.stream()
            .filter(r -> "UserRating".equalsIgnoreCase(r.getSource()))
            .findFirst()
            .orElseGet(() -> {
              Rating newRating = new Rating();
              newRating.setSource("UserRating");
              newRating.setMovie(movie);
              ratings.add(newRating);
              return newRating;
            });

    userRating.setValue(String.valueOf(rating));
    movie.setUpdatedAt(new Date());
    movieRepository.save(movie);
  }

  public List<Movie> getTopRatedMovies() {
    return movieRepository.findTopRatedMoviesOrderedByBoxOffice(PageRequest.of(0, 10));
  }

  private void updateMovieWithOmdbData(Movie movie, OmdbResponse response) {
    List<Rating> ratings = createRatings(response.getRatings(), movie);
    ratingRepository.saveAll(ratings);
    movie.getRatings().addAll(ratings);
    movie.setBoxOffice(parseBoxOffice(response.getBoxOffice()));
    movie.setWonOscar(hasWonOscar(response.getAwards()));
    movie.setUpdatedAt(new Date());
    movieRepository.save(movie);
  }

  private Movie saveNewMovie(OmdbResponse response) {
    Movie movie = new Movie();
    movie.setTitle(response.getTitle());
    movie.setBoxOffice(parseBoxOffice(response.getBoxOffice()));
    movie.setWonOscar(hasWonOscar(response.getAwards()));
    movie.setCreatedAt(new Date());

    movie = movieRepository.save(movie);

    List<Rating> ratings = createRatings(response.getRatings(), movie);
    ratingRepository.saveAll(ratings);
    movie.getRatings().addAll(ratings);

    return movie;
  }

  private List<Rating> createRatings(List<OmdbRating> omdbRatings, Movie movie) {
    return omdbRatings.stream().map(r -> {
      Rating rating = new Rating();
      rating.setSource(r.getSource());
      rating.setValue(r.getValue());
      rating.setMovie(movie);
      return rating;
    }).toList();
  }

  private long parseBoxOffice(String boxOffice) {
    try {
      return Long.parseLong(boxOffice.replaceAll("[$,]", ""));
    } catch (Exception e) {
      return 0;
    }
  }

  private boolean hasWonOscar(String awards) {
    return awards != null && OSCAR_PATTERN.matcher(awards).find();
  }

}
