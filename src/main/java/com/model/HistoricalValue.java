package com.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HistoricalValue implements Comparable<HistoricalValue> {
    Long timestamp;
    BigDecimal value;
    Instruction instruction;

    public HistoricalValue(Long timestamp, BigDecimal value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public HistoricalValue(Long timestamp, BigDecimal value, Instruction instruction) {
        this.timestamp = timestamp;
        this.value = value;
        this.instruction = instruction;
    }

    @Override
    public int compareTo(HistoricalValue o) {
        Long l = (this.timestamp - o.timestamp);
        return l.intValue();

    }
}
