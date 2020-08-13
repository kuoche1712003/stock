package io.kuoche.stock.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class StockDto {
    private LocalDate date;
    private Long numberOfStock;
    private BigDecimal startPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal endPrice;
    private BigDecimal calculate;
}
