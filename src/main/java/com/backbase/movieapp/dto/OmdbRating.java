package com.backbase.movieapp.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OmdbRating {
  @SerializedName("Source")
  private String source;

  @SerializedName("Value")
  private String value;
}
