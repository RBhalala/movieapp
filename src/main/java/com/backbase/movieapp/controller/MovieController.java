package com.backbase.movieapp.controller;

import com.backbase.movieapp.model.Movie;
import com.backbase.movieapp.service.MovieService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@AllArgsConstructor
@Validated
public class MovieController {
  private final MovieService movieService;
  public record MovieAwardResponse(String title, boolean won, String message) {}

  @GetMapping("{title}")
  public ResponseEntity<Movie> getMovieByTitle(@PathVariable String title) {
    Movie response = movieService.getMovieByTitle(title);
    return ResponseEntity.ok(response);
  }

  @GetMapping("{title}/best-picture")
  public ResponseEntity<MovieAwardResponse> checkBestPicture(@PathVariable String title) {
    Movie response = movieService.getMovieByTitle(title);
    String message = String.format("The movie '%s' %s the Best Picture Oscar",
            title,
            response.isWonOscar()  ? "won" : "did not win");
    return ResponseEntity.ok(new MovieAwardResponse(title, response.isWonOscar(), message));
  }

  @PostMapping("{title}/rate")
  public ResponseEntity<Void> rateMovie(
          @PathVariable String title,
          @RequestParam @Min(1) @Max(10) double rating
  ) {
    movieService.rateMovie(title, rating);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/top-rated")
  public ResponseEntity<List<Movie>> getTopRated() {
    List<Movie> top = movieService.getTopRatedMovies();
    return ResponseEntity.ok(top);
  }
}