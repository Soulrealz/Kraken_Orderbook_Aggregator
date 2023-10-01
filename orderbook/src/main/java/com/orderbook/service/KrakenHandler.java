package com.orderbook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderbook.model.OrderBookWebsocketDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class KrakenHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(KrakenHandler.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private WebSocketSession clientSession;

    public KrakenHandler() {}

    public void sendAndConfirm(String message) {
        try {
            clientSession.sendMessage(new TextMessage(message));
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Object jsonResponseObject = jsonMapper.readValue(message.getPayload(), Object.class);
            if (jsonResponseObject instanceof List jsonList) {
                OrderBookWebsocketDTO orderBookDTO = jsonMapper.convertValue(jsonList.get(1), OrderBookWebsocketDTO.class);
                System.out.println(orderBookDTO.toString());
                System.out.println(jsonList.get(3) + "\n\n\n");
            }
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public boolean connect() {
        try {
            var webSocketClient = new StandardWebSocketClient();
            clientSession = webSocketClient.execute(
                    this,
                    new WebSocketHttpHeaders(),
                    URI.create("wss://ws.kraken.com")).get();
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception while creating websockets", e);
        }
        return false;
    }

    public void close() {
        try {
            clientSession.close();
            LOGGER.info("Session closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOGGER.info("Connection closed");
    }

    public boolean connected() {
        return clientSession != null && clientSession.isOpen();
    }

}
