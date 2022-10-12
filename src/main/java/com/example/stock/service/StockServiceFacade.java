package com.example.stock.service;

import com.example.stock.repository.LockRepository;
import com.example.stock.repository.RedisLockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StockServiceFacade {
    private final StockService stockService;

    private final LockRepository lockRepository;

    private final RedisLockRepository redisLockRepository;

    public StockServiceFacade(StockService stockService, LockRepository lockRepository, RedisLockRepository redisLockRepository) {
        this.stockService = stockService;
        this.lockRepository = lockRepository;
        this.redisLockRepository = redisLockRepository;
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

    @Transactional
    public void decrease_namedLock(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decrease_namedLock(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }

    public void decrease_redis_lettuceLock(Long key, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease_sync(key, quantity);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}
