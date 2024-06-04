package com.ll.clearpath.domain.tourlist.tourlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourlistMapDto {
    private String title;

    private double latitude;

    private double longitude;

    private double distance;

    private String address;

    private String tag;

    private double currentTemperature;

    private String weatherCondition;
}
