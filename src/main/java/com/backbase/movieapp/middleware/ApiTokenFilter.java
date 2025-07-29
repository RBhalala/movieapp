package com.backbase.movieapp.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ApiTokenFilter extends HttpFilter {

  @Value("${api.token}")
  private String apiToken;

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    try {
      String token = request.getHeader("X-API-TOKEN");

      if (token == null || !token.equals(apiToken)) {
        log.info("Invalid token, rejecting request");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API token");
      } else
        chain.doFilter(request, response);
    } catch (Exception e) {
      log.error("doFilter: Error while authenticating {}", String.valueOf(e));
      throw e;
    }
  }
}
