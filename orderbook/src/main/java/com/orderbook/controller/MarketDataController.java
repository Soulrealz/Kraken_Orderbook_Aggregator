package com.orderbook.controller;

import com.orderbook.service.KrakenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MarketDataController {
    private final KrakenService krakenService;

    @Autowired
    public MarketDataController(KrakenService krakenService) {
        this.krakenService = krakenService;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connect() {
        return krakenService.connect();
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(
            @RequestParam(value = "pair", required = false) List<String> pairs,
            @RequestParam(value = "interval", defaultValue = "5") int interval,
            @RequestParam(value = "depth", defaultValue = "10") int depth,
            @RequestParam("name") String name
    ) {
        return krakenService.subscribe(pairs, interval, depth, name);
    }

    @PostMapping ("/close")
    public ResponseEntity<String> close() {
        return krakenService.close();
    }
}
