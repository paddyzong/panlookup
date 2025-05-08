package com.cardlookup.panlookup.controller;

import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.service.CardRangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CardRangeController {

    private final CardRangeService service;

    public CardRangeController(CardRangeService service) { this.service = service; }

    @GetMapping("/card-range/{pan:\\d{13,19}}")
    public ResponseEntity<CardRange> find(@PathVariable String pan) {
        return service.findByPan(pan)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
