package com.ll.clearpath.domain.tourlist.tourlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TourlistResponseDto {
    private int result;

    private String resultMessage;

    private int totalCount;

    private int resultCount;

    private int pageSize;

    private int pageCount;

    private int currentPage;

    private List<TourlistDetailDto> items;


}
