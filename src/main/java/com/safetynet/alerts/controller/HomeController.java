package com.safetynet.alerts.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;


@Hidden
@RestController
public class HomeController {

    @GetMapping("/")
    public RedirectView redirectWithRedirectView() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
