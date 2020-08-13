package io.kuoche.stock.controller;

import io.kuoche.stock.model.StockType;
import io.kuoche.stock.model.dto.StockDto;
import io.kuoche.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/Stock")
    public @ResponseBody List<StockDto> getStock(
            @RequestParam String stockNumber,
            @RequestParam StockType stockType){
        return stockService.getStock(stockNumber, stockType);
    }
}
