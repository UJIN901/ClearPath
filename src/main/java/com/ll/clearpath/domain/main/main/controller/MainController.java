package com.ll.clearpath.domain.main.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
//@PreAuthorize("isAuthenticated()")
public class MainController {
    @Value("${maps.kakao.javascript-key}")
    private String kakaoMapsApiKey;

    @GetMapping("/")
    public String showMain(Model model){
        return "domain/main/main/main";
    }

    @GetMapping("/map")
    public String showMap(Model model){
        model.addAttribute("kakaoMapsApiKey", kakaoMapsApiKey);
        return "domain/main/main/map";
    }
}
