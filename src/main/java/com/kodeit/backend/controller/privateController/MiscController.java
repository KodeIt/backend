package com.kodeit.backend.controller.privateController;

import com.kodeit.backend.repository.CountryRepository;
import com.kodeit.backend.repository.StateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/private/misc")
public class MiscController {

    private final StateRepository stateRepository;

    private final CountryRepository countryRepository;

    @Autowired
    public MiscController(StateRepository stateRepository, CountryRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping("/states")
    public ResponseEntity<?> getStatesByCountry(@RequestParam("countryId") Long countryId) {
        try {
            return ResponseEntity.ok().body(stateRepository.findAllByCountryId(countryId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/countries")
    public ResponseEntity<?> getCountries() {
        try {
            return ResponseEntity.ok().body(countryRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
