package com.cardlookup.panlookup.controller;

import com.cardlookup.panlookup.dto.CardRangeResult;
import com.cardlookup.panlookup.entity.CardRange;
import com.cardlookup.panlookup.service.CardRangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CardRangeController {

    private static final Logger log = LoggerFactory.getLogger(CardRangeController.class);

    private final CardRangeService service;

    public CardRangeController(CardRangeService service) {
        this.service = service;
    }

    @GetMapping("/card-range/{pan:\\d{13,19}}")
    public ResponseEntity<CardRangeResult> find(@PathVariable String pan) {
        log.info("Looking up PAN: {}", pan);
        CardRangeResult result = service.findByPan(pan)
                .map(range -> new CardRangeResult(true, range))
                .orElse(new CardRangeResult(false, null));
        return ResponseEntity.ok(result);
    }
}
