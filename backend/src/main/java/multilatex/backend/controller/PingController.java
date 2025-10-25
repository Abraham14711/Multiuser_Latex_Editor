package multilatex.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PingController {

    @GetMapping("/ping/{id}")
    public ResponseEntity<String> ping(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body("pong");
    }
}
