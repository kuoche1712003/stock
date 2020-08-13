package io.kuoche.stock.service;

import io.kuoche.stock.model.StockType;
import io.kuoche.stock.model.dto.StockDto;

import java.util.List;

public interface StockService {
    List<StockDto> getStock(String stockNumber, StockType stockType);
}
