package com.ll.clearpath.domain.main.main.controller;

import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistMapDto;
import com.ll.clearpath.domain.tourlist.tourlist.service.TourlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class MainController {
    @Value("${maps.kakao.javascript-key}")
    private String kakaoMapsApiKey;

    private final TourlistService tourlistService;

    @GetMapping("/")
    public String showMain(Model model){
        model.addAttribute("kakaoMapsApiKey", kakaoMapsApiKey);

        double defaultRadius = 3.0;
        List<TourlistMapDto> tourlist = tourlistService.getTourlistByDistance(defaultRadius);
        model.addAttribute("tourlist", tourlist);
        return "domain/main/main/main";
    }
}
