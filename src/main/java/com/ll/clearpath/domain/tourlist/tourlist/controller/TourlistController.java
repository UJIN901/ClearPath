package com.ll.clearpath.domain.tourlist.tourlist.controller;

import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistMapDto;
import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistRequestDto;
import com.ll.clearpath.domain.tourlist.tourlist.service.TourlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/tourlist")
@Slf4j
public class TourlistController {
    private final TourlistService tourlistService;

    @Value("${maps.visitJeju.apiKey}")
    private String tourlistApiKey;

    @GetMapping("")
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

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<TourlistMapDto>> searchTours(
            @RequestParam(value = "category") String category,
            @RequestParam(value = "search") String search,
            @RequestParam(value = "radius") String radius,
            @RequestParam(value = "weather") String weather) {

        double radiusValue = "all".equalsIgnoreCase(radius) ? 0 : Double.parseDouble(radius);
        List<TourlistMapDto> result = tourlistService.searchTours(category, search, radiusValue, weather);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content 상태 코드 반환
        }

        return ResponseEntity.ok(result); // 200 OK 상태 코드와 함께 결과 반환
    }
}
