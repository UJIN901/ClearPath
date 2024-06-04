package com.ll.clearpath.domain.weather.weather.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherRequestDto {
    @NotBlank(message = "serviceKey는 필수 입력값입니다.")
    private String serviceKey;

    @NotBlank(message = "pageNo는 필수 입력값입니다.")
    private int pageNo;

    @NotBlank(message = "numOfRows는 필수 입력값입니다.")
    private int numOfRows;

    @NotBlank(message = "dataType은 필수 입력값입니다.")
    private String dataType;

    @NotBlank(message = "base_date은 필수 입력값입니다.")
    private String base_date;

    @NotBlank(message = "base_time은 필수 입력값입니다.")
    private String base_time;

    @NotBlank(message = "nx는 필수 입력값입니다.")
    private int nx;

    @NotBlank(message = "ny는 필수 입력값입니다.")
    private int ny;


}
