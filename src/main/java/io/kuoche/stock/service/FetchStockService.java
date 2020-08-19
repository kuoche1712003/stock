package io.kuoche.stock.service;

import io.kuoche.stock.model.DailyTransaction;

import java.util.List;

public interface FetchStockService {
    List<DailyTransaction> fetchListedStock(String stockNumber, String date);
    List<DailyTransaction> fetchOpcStock(String stockNumber, String date);
}
