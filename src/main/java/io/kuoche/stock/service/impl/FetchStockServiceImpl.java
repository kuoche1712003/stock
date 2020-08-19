package io.kuoche.stock.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kuoche.stock.model.DailyTransaction;
import io.kuoche.stock.model.dto.ListedStockDto;
import io.kuoche.stock.model.dto.OpcStockDto;
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
    private final String listedUrl;
    private final String opcUrl;
    private final ObjectMapper mapper;

    public FetchStockServiceImpl(
            RestTemplate restTemplate,
            @Value("${listed.url}") String listedUrl,
            @Value("${opc.url}")String opcUrl,
            ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.listedUrl = listedUrl;
        this.opcUrl = opcUrl;
        this.mapper = mapper;
    }

    @Override
    public List<DailyTransaction> fetchListedStock(String stockNumber, String date) {
        String url = String.format("%s/STOCK_DAY?response=json&date=%s&stockNo=%s", listedUrl, date, stockNumber);
        ListedStockDto data = restTemplate.getForObject(url, ListedStockDto.class);
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

    @Override
    public List<DailyTransaction> fetchOpcStock(String stockNumber, String date) {
        String url = String.format("%s/aftertrading/daily_trading_info/st43_result.php?l=zh-tw&d=%s&stkno=%s", opcUrl, date, stockNumber);
        String json = restTemplate.getForObject(url, String.class);
        json = json.replace("\\", "");
        OpcStockDto data = new OpcStockDto();
        data.setData(new ArrayList<>());
        try{
            data = mapper.readValue(json, OpcStockDto.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<DailyTransaction> result = new ArrayList<>();

        for(List<String> row : data.getData()){
            DailyTransaction dailyTransaction = new DailyTransaction();
            dailyTransaction.setDate(toLocalDate(row.get(0)));
            dailyTransaction.setNumberOfStock(Long.valueOf(remove(row.get(1))) * 1000L);
            dailyTransaction.setStartPrice(new BigDecimal(row.get(3)));
            dailyTransaction.setHighestPrice(new BigDecimal(row.get(4)));
            dailyTransaction.setLowestPrice(new BigDecimal(row.get(5)));
            dailyTransaction.setEndPrice(new BigDecimal(row.get(6)));

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
