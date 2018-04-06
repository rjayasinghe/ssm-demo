package org.synyx.coffee.ssmdemo.registration.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {

        this.registrationService = registrationService;
    }

    @PutMapping("/registration")
    public void createRegistration(@RequestBody RegistrationDto registrationDto) {

        registrationService.startRegistration(registrationDto);
    }


    @PostMapping("/registration/accept")
    public void acceptRegistration(@RequestBody RegistrationDto registrationDto) {

        registrationService.acceptRegistration(registrationDto);
    }
}
