package com.example.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class StockServiceFacade {
    private final StockService stockService;

    public StockServiceFacade(StockService stockService) {
        this.stockService = stockService;
    }

    public void decrease_optimisticLock(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decreaseByOptimisticLock(id, quantity);

                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
