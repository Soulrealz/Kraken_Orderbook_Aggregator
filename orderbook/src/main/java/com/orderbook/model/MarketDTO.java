package com.orderbook.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"price", "volume", "timestamp" , "r"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDTO {

    public Double price;
    public Double volume;
    public String timestamp;
    public String r;

    private MarketDTO() {}

    public Double getPrice() {
        return price;
    }
    public Double getVolume() {
        return volume;
    }

    public Instant convertTime() {
        String toBeConverted;
        if (timestamp.contains(".")) {
            String[] result = timestamp.split("\\.");
            toBeConverted = result[0];
        } else {
            toBeConverted = timestamp;
        }

        return Instant.ofEpochMilli(Long.parseLong(toBeConverted) * 1000);
    }

    @Override
    public String toString() {
        return " [ " +
                price +
                ", " +
                Math.floor(volume * 100) / 100 +
                " ]";
    }
}
