package io.kuoche.stock.service.impl;

import io.kuoche.stock.model.DailyTransaction;
import io.kuoche.stock.model.dto.DailyTransactionDto;
import io.kuoche.stock.service.FetchStockService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service

public class FetchStockServiceImpl implements FetchStockService {
    private final RestTemplate restTemplate;
    private final String stockUrl;

    public FetchStockServiceImpl(
            RestTemplate restTemplate,
            @Value("${stock.url}") String stockUrl) {
        this.restTemplate = restTemplate;
        this.stockUrl = stockUrl;
    }

    @Override
    public List<DailyTransaction> fetchDailyTransaction(String stockNumber, String date) {
        String url = String.format("%s/STOCK_DAY?response=json&date=%s&stockNo=%s", stockUrl, date, stockNumber);
        DailyTransactionDto data = restTemplate.getForObject(url, DailyTransactionDto.class);
        List<DailyTransaction> result = new ArrayList<>();

        for(List<String> row : data.getData()){
            List<String> fields = data.getFields();
            DailyTransaction dailyTransaction = new DailyTransaction();

            int index = fields.indexOf("日期");
            dailyTransaction.setDate(toLocalDate(row.get(index)));

            index = fields.indexOf("成交股數");
            dailyTransaction.setNumberOfStock(Long.valueOf(remove(row.get(index))));

            index = fields.indexOf("開盤價");
            dailyTransaction.setStartPrice(new BigDecimal(remove(row.get(index))));

            index = fields.indexOf("最高價");
            dailyTransaction.setHighestPrice(new BigDecimal(remove(row.get(index))));

            index = fields.indexOf("最低價");
            dailyTransaction.setLowestPrice(new BigDecimal(remove(row.get(index))));

            index = fields.indexOf("收盤價");
            dailyTransaction.setEndPrice(new BigDecimal(remove(row.get(index))));

            result.add(dailyTransaction);
        }
        return result;
    }

    private String remove(String target){
        return target.replace(",","");
    }

    private LocalDate toLocalDate(String dateString){
        String[] dateItems = dateString.split("/");
        int year = Integer.valueOf(dateItems[0]) + 1911;
        int month = Integer.valueOf(dateItems[1]);
        int day = Integer.valueOf(dateItems[2]);
        return LocalDate.of(year, month, day);
    }
}
