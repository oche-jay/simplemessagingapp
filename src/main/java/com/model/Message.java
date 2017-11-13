package com.model;

import com.model.util.JsonRequired;
import com.model.util.PositiveValue;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Message {
    @JsonRequired
    String product;
    @JsonRequired
    @PositiveValue
    BigDecimal value;
    @PositiveValue
    int occurrence = 1;
    Instruction instruction;
}


