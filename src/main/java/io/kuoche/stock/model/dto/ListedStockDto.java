package io.kuoche.stock.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ListedStockDto {
    private List<String> fields;
    private List<List<String>> data;
}
