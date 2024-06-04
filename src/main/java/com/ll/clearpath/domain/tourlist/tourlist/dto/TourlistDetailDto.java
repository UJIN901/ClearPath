package com.ll.clearpath.domain.tourlist.tourlist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TourlistDetailDto {
    private String title;

    private String address;

    private String tag;

    private String introduction;

    private double latitude;

    private double longitude;

    private String imgpath;

    @SuppressWarnings("unchecked")
    @JsonProperty("repPhoto")
    private void unpackNested(Map<String, Object> repPhoto) {
        Map<String, String> photoid = (Map<String, String>) repPhoto.get("photoid");
        this.imgpath = photoid.get("imgpath");
    }

}
