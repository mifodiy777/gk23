package ru.kircoop.gk23.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kircoop.gk23.entity.Garag;

import java.util.Arrays;
import java.util.List;

@RestController
public class GaragController {

    @GetMapping(value = "/garags")
    public ResponseEntity<List<Garag>> read() {
        Garag garag = new Garag();
        List<Garag> garags = Arrays.asList(garag);
        return garags != null && !garags.isEmpty()
                ? new ResponseEntity<>(garags, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
