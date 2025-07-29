package com.backbase.movieapp.externalservice;

import com.backbase.movieapp.dto.OmdbResponse;
import com.backbase.movieapp.middleware.OmdbApiException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class OmdbApi {
  @Value("${omdb.api.token}")
  private String apiKey;

  public OmdbResponse fetchMovieData(String title) {
    try {
      String urlString = "http://www.omdbapi.com/?t=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
              + "&apikey=" + apiKey;

      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder(URI.create(urlString)).GET().build();
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        throw new BadRequestException("Failed : HTTP error code : " + response.statusCode());
      }
      Gson gson = new Gson();
      OmdbResponse res = gson.fromJson(response.body(), OmdbResponse.class);
      if (res == null || !"True".equalsIgnoreCase(res.getResponse())) {
        String errorMsg = res != null ? res.getError() : "Unknown error";
        throw new BadRequestException("OMDb API Error: " + errorMsg);
      }
      return res;
    } catch (IOException | InterruptedException e) {
      log.error("Omdb API Error", e);
      throw new OmdbApiException(e.getMessage());
    }
  }
}
