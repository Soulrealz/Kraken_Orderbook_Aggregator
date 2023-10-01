package com.orderbook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Random;

@Service
public class KrakenService extends TextWebSocketHandler {
    private final KrakenHandler krakenHandler;

    public KrakenService()  {
        krakenHandler = new KrakenHandler();
    }

    public ResponseEntity<String> subscribe(List<String> pairs, int interval, int depth, String name) {
        final ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("event", "subscribe");
        final int reqId = Math.abs(new Random().nextInt());
        json.put("reqid", reqId);

        if (pairs != null) {
            json.putArray("pair").addAll((ArrayNode) new ObjectMapper().valueToTree(pairs));
        }

        ObjectNode subscription = json.putObject("subscription");
        subscription.put("name", name);

        try {
            krakenHandler.sendAndConfirm(json.toString());
            return ResponseEntity.ok("subscribed");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("error:\"" + e.getMessage() + "\"");
        }
    }

    public ResponseEntity<String> connect() {
        boolean success = krakenHandler.connected()
                ? krakenHandler.connected()
                : krakenHandler.connect();

        if (success) {
            return ResponseEntity.ok("Connected to Kraken");
        } else {
            return ResponseEntity.ok("Cannot connect to Kraken");
        }
    }

    public ResponseEntity<String> close() {
        krakenHandler.close();
        return ResponseEntity.ok("Closed connection to Kraken");
    }
}
