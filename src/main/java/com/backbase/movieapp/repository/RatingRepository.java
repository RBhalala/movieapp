package com.backbase.movieapp.repository;

import com.backbase.movieapp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {}
