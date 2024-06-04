package com.ll.clearpath.domain.weather.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponseDto {

    @JsonProperty("response")
    private Response response;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Response {
        @JsonProperty("header")
        private ResponseHeader header;

        @JsonProperty("body")
        private ResponseBody body;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ResponseHeader {
        @JsonProperty("resultCode")
        private int resultCode;

        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ResponseBody {
        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("items")
        private Items items;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Items {
        @JsonProperty("item")
        private List<WeatherDetailDto> item;
    }

}
