package io.kuoche.stock.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpcStockDto {
    @JsonProperty("aaData")
    private List<List<String>> data;
}
