package com.ll.clearpath.domain.tourlist.tourlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TourlistModalDto {
    private String title;

    private String address;

    private String tag;

    private String introduction;

    private String imgpath;

    private double currentTemperature;

    private String weatherCondition;
}
