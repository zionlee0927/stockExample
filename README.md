## 멀티 스레드 동시성 문제


### Race Condition issue
둘 이상의 스레드가 고융 데이터에 엑세스할 수 있고 동시에 변경하려고 할 때 발생하는 문제

- 둘 이상의 스레드 : 요청
- 공유 데이터 : Stock id가 1의 데이터 
- 동시에 변경 : 멀티 스레드 환경에서 stock id 1 에 대한 재고 감소 업데이트
- 발생하는 문제 : 기대하는 값으로 정상적으로 바뀌지 않음

### 문제 해결

#### 1. 하나의 스레드에서만 데이터에 엑세스 가능하게 한다.

java의 synchronized를 이용하여 해당 메서드의 스레드를 제한한다. 

```java
@Transactional
public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.save(stock);
}
```

하지만 @Transactional 으로 인해 정상 동작하지 않음

- 해당 어노테이션은 Spring의 AOP를 이용하므로 새로운 프록시를 생성하기 때문에 동기화 된 메소드와 별개로 커밋 전에 메소드가 호출이 될수 있기 때문에 Race Condition을 보장할 수 없음

또한 가장 큰 문제는 여러 서버를 사용하게 될 때 인스턴스 단위로는 thread-safe 보장되기 때문에

Race Condition이 발생하게 됨
- 예시

| Time  | sever1      | stock | server2     |
|-------|-------------|-------|-------------|
| 10:00 | data access | 5     | -           |
| -     | -           | 5     | data access |
| 10:01 | update      | 4     | -           |
| -     | -           | 4     | update      |



