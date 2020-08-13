package io.kuoche.stock.service.impl;

import io.kuoche.stock.model.DailyTransaction;
import io.kuoche.stock.model.StockType;
import io.kuoche.stock.model.dto.StockDto;
import io.kuoche.stock.service.FetchStockService;
import io.kuoche.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final FetchStockService fetchStockService;
    private final ModelMapper modelMapper;

    @Override
    public List<StockDto> getStock(String stockNumber, StockType stockType) {
        LocalDate now = LocalDate.now();
        LocalDate twentyDaysAgo = now.plusMonths(-1L);
        LocalDate fortyDaysAgo = now.plusMonths(-2L);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        Set<DailyTransaction> set = new HashSet<>();

        if(stockType == StockType.LISTED){
            set.addAll(fetchStockService.fetchDailyTransaction(stockNumber, now.format(dateTimeFormatter)));
            set.addAll(fetchStockService.fetchDailyTransaction(stockNumber, twentyDaysAgo.format(dateTimeFormatter)));
            set.addAll(fetchStockService.fetchDailyTransaction(stockNumber, fortyDaysAgo.format(dateTimeFormatter)));
        }

        List<DailyTransaction> result = new ArrayList<>(set).stream()
                .sorted(Comparator.comparing(DailyTransaction::getDate))
                .collect(Collectors.toList());

        BigDecimal preEndPrice = result.get(0).getEndPrice();
        for(int i = 1 ; i < result.size() ; i++){
            DailyTransaction target = result.get(i);
            target.setPreEndPrice(preEndPrice);
            preEndPrice = target.getEndPrice();
        }

        BigDecimal totalBuy = result.stream()
                .skip(1)
                .limit(20)
                .map(item -> item.getBuyNumberOfStock())
                .reduce((total, item) -> total.add(item))
                .get();

        BigDecimal totalSell = result.stream()
                .skip(1)
                .limit(20)
                .map(item -> item.getSellNumberOfStock())
                .reduce((total, item) -> total.add(item))
                .get();

        int preIndex = 1;
        for(int i = 21 ; i < result.size() ; i++){
            DailyTransaction target = result.get(i);
            DailyTransaction pre = result.get(preIndex);
            totalBuy = totalBuy.add(target.getBuyNumberOfStock()).subtract(pre.getBuyNumberOfStock());
            totalSell = totalSell.add(target.getSellNumberOfStock()).subtract(pre.getSellNumberOfStock());
            BigDecimal calculate = totalSell.divide(totalBuy, 4, RoundingMode.HALF_DOWN);
            target.setCalculate(calculate);
            preIndex++;
        }

        return result.stream().skip(21)
                .map(item->modelMapper.map(item, StockDto.class))
                .collect(Collectors.toList());
    }
}
