package com.backbase.movieapp.middleware;

public class OmdbApiException extends RuntimeException {
  public OmdbApiException(String message) {
    super(message);
  }
}