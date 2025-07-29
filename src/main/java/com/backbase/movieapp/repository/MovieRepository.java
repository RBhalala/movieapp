package com.backbase.movieapp.repository;

import com.backbase.movieapp.model.Movie;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

  Optional<Movie> findByTitleIgnoreCase(String title);

  @Query("""
    SELECT m FROM Movie m
    JOIN m.ratings r
    WHERE r.source = 'UserRating'
    ORDER BY CAST(r.value AS double) DESC, m.boxOffice DESC
    """)
  List<Movie> findTopRatedMoviesOrderedByBoxOffice(Pageable pageable);

}
