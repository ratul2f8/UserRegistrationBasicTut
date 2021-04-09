package com.example.spring.registration.controllers;

import com.example.spring.registration.models.RegistrationRequest;
import com.example.spring.registration.services.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest registrationRequest){
        //System.out.println("Got it");
        //System.out.println(registrationRequest);
        return  registrationService.register(registrationRequest);
    }
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
