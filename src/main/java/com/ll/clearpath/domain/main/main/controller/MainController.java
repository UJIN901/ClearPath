package com.ll.clearpath.domain.main.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
//@PreAuthorize("isAuthenticated()")
public class MainController {

    @GetMapping("/")
    public String showMain(Model model){
        return "domain/main/main/main";
    }
}
