package com.ll.clearpath.domain.weather.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDetailDto {
    private String category;

    private String fcstTime;

    private String fcstValue;
}
