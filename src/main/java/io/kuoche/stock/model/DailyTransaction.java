package io.kuoche.stock.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class DailyTransaction {
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Long numberOfStock;
    private BigDecimal startPrice;
    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
    private BigDecimal endPrice;
    private BigDecimal preEndPrice;
    private BigDecimal calculate;

    public BigDecimal getBuyPress(){
        BigDecimal first = this.startPrice.subtract(this.preEndPrice);
        BigDecimal second = this.highestPrice.subtract(this.lowestPrice);
        return first.add(second);
    }

    public BigDecimal getSellPress(){
        BigDecimal first = this.startPrice.subtract(this.lowestPrice);
        BigDecimal second = this.highestPrice.subtract(this.endPrice);
        return first.add(second);
    }

    public BigDecimal getBuyNumberOfStock(){
        BigDecimal buy = this.getBuyPress();
        BigDecimal sell = this.getSellPress();

        return buy.divide(buy.abs().add(sell.abs()), 4, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(this.numberOfStock));
    }

    public BigDecimal getSellNumberOfStock(){
        BigDecimal buy = this.getBuyPress();
        BigDecimal sell = this.getSellPress();

        return sell.divide(buy.abs().add(sell.abs()), 4, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(this.numberOfStock));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyTransaction that = (DailyTransaction) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "DailyTransaction{" +
                "date=" + date +
                ", numberOfStock=" + numberOfStock +
                ", startPrice=" + startPrice +
                ", highestPrice=" + highestPrice +
                ", lowestPrice=" + lowestPrice +
                ", endPrice=" + endPrice +
                ", preEndPrice=" + preEndPrice +
                ", calculate=" + calculate +
                '}';
    }
}
