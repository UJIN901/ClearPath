package com.ll.clearpath.domain.tourlist.tourlist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TourlistRequestDto {
    @NotBlank(message = "apiKey는 필수 입력값입니다.")
    private String apiKey;

    @NotBlank(message = "locale은 필수 입력값입니다.")
    private String locale;

    private String category;

    private int page;
}
