package com.backbase.movieapp.service;


import com.backbase.movieapp.model.Movie;
import com.backbase.movieapp.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class CsvDataLoader {

  private final MovieRepository movieRepository;

  public CsvDataLoader(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  @PostConstruct
  public void loadCsv() {
    try {
      ClassPathResource resource = new ClassPathResource("academy_awards.csv");
      BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
      CSVFormat format = CSVFormat.DEFAULT.builder()
              .setHeader("Year", "Category", "Nominee", "Additional Info", "Won?")
              .setSkipHeaderRecord(true)
              .build();

      Iterable<CSVRecord> records = format.parse(reader);

      for (CSVRecord record : records) {
        String category = record.get("Category");
        if ("Best Picture".equalsIgnoreCase(category)) {
          String title = record.get("Nominee");
          if(movieRepository.findByTitleIgnoreCase(title).isEmpty()) {
            String won = record.get("Won?");


            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setWonOscar("YES".equalsIgnoreCase(won));
            movieRepository.save(movie);
          }
        }
      }
      System.out.println("✅ CSV loaded successfully.");

    } catch (Exception e) {
      System.err.println("❌ Failed to load CSV: " + e.getMessage());
    }
  }
}
