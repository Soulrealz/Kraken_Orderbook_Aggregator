package com.orderbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookWebsocketDTO {
    //asks
    public List<MarketDTO> a;
    //bids
    public List<MarketDTO> b;

    //asks snapshot
    public List<MarketDTO> as;
    //bids snapshot
    public List<MarketDTO> bs;

    public MarketDTO getBestAsk() {
        List<MarketDTO> asksCopy = null;

        // These conditions apply many times because each time the user input may be different
        // The incoming array in the response may be: a, b, as or bs
        // Unknown properties are ignored thanks to the annotation, and we take what we have
        if (as != null || a != null) {
            asksCopy = new ArrayList<>(as != null ? as : a);
        }

        return asksCopy
                .stream()
                .min(Comparator.comparing(MarketDTO::getPrice))
                .orElseThrow(NoSuchElementException::new);
    }

    public MarketDTO getBestBid() {
        List<MarketDTO> bidsCopy = null;

        if (bs != null || b != null) {
            bidsCopy = new ArrayList<>(bs != null ? bs : b);
        }

        assert bidsCopy != null;
        return bidsCopy
                .stream()
                .max(Comparator.comparing(MarketDTO::getPrice))
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Instant timeStamp = null;

        if (as != null || a != null) {
            stringBuilder.append("<------------------------------------>\n");
            stringBuilder.append("asks: \n");

            List<MarketDTO> reversedAsks = new ArrayList<>(as != null ? as : a);
            Collections.reverse(reversedAsks);

            for (MarketDTO market : reversedAsks) {
                stringBuilder.append(market).append(",\n");
                timeStamp = market.convertTime();
            }

            stringBuilder.append("best ask: [ ")
                    .append(getBestAsk().getPrice())
                    .append(", ")
                    .append(Math.floor(getBestAsk().getVolume() * 100) / 100)
                    .append(" ]\n");
        }

        if (bs != null || b != null) {
            stringBuilder.append("best bid: [ ")
                    .append(getBestBid().getPrice())
                    .append(", ")
                    .append(Math.floor(getBestBid().getVolume() * 100) / 100)
                    .append(" ]\n");

            stringBuilder.append("bids: \n");

            List<MarketDTO> bidsCopy = new ArrayList<>(bs != null ? bs : b);
            for (MarketDTO market : bidsCopy) {
                stringBuilder.append(market).append(",\n");
                timeStamp = market.convertTime();
            }
        }

        stringBuilder.append(">-------------------------------------<\n");
        stringBuilder.append(timeStamp);
        return stringBuilder.toString();
    }
}
