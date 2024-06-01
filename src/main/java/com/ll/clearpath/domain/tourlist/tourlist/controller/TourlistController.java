package com.ll.clearpath.domain.tourlist.tourlist.controller;

import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistRequestDto;
import com.ll.clearpath.domain.tourlist.tourlist.service.TourlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class TourlistController {
    private final TourlistService tourlistService;

    @Value("${maps.visitJeju.apiKey}")
    private String tourlistApiKey;

    @GetMapping("/api/tourlist")
    @ResponseBody
    public ResponseEntity<Void> connectTourlistOpenApi() {

        TourlistRequestDto tourlistRequestDto = new TourlistRequestDto();
        tourlistRequestDto.setApiKey(tourlistApiKey);
        tourlistRequestDto.setLocale("kr");
        tourlistRequestDto.setCategory("c1");

        try {
            tourlistService.save(tourlistRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
