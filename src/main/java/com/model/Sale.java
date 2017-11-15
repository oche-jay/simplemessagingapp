package com.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
public class Sale {
    String productType;
    BigDecimal totalItemsSold = new BigDecimal(0);
    BigDecimal currentValue = new BigDecimal(0);
    BigDecimal runningTotal = new BigDecimal(0);
    SortedSet<HistoricalValue> historicalValues = new TreeSet<>();
}


