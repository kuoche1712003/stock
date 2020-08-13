package io.kuoche.stock.service;

import io.kuoche.stock.model.DailyTransaction;

import java.util.List;

public interface FetchStockService {
    List<DailyTransaction> fetchDailyTransaction(String stockNumber, String date);
}
