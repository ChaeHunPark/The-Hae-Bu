# 트랜잭션 전파 (REQUIRES_NEW)

## 1. 개요
- 목표 : 부모 트랜잭션이 롤백되어도 특정 작업만 따로 저장하는 방법 찾기
- 가설 : Propagation.REQUIRES_NEW를 사용하면 부모와 독립된 새로운 트랜잭션을 생성하여 생존 할 수 있음

---

## 2. 코드 구조
- 서비스 코드
  - LogService
    ```java

    @Service
    public class LogService {
    @Autowired private LogRepository logRepository;
    
        // 부모와 별개로 자신만의 트랜잭션을 새로 만든다.
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void 로그_저장(String message) {
            logRepository.save(new Log(message));
        }
    
    }
    ```

  - OrderService

      ```java
      @Service
      public class OrderService {
          @Autowired private OrderRepository orderRepository;
          @Autowired private LogService logService;
    
          @Transactional
          public void 주문_진행 (String orderName) {
              orderRepository.save(new Order(orderName)); // 1. 주문 저장
              logService.로그_저장(orderName + " 로그");
    
              throw new RuntimeException("주문 처리 중 갑작스러운 서버 다운");
          }
      }
      ```
- 테스트 코드
  ```java
    @Test
    void 실험5_전파_속성_테스트() {
        try {
            orderService.주문_진행("치킨");
        } catch (Exception e) {
            System.out.println("예외 : " + e.getMessage());
        }
        System.out.println("----- DB 결과 -----");
        System.out.println("주문 갯수 : " + orderRepository.count());
        System.out.println("로그 갯수 : " + logRepository.count());
    }
  ```
- 테스트 결과
  ```java
  Hibernate: insert into orders (product) values (?)
  Hibernate: insert into log (message) values (?)
  예외 : 주문 처리 중 갑작스러운 서버 다운
  ----- DB 결과 -----
  Hibernate: select count(*) from orders o1_0
  주문 갯수 : 0
  Hibernate: select count(*) from log l1_0
  로그 갯수 : 1
  Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0
  ```
  
---
## 3. 결과
- 결과 : 주문 데이터는 롤백 0건, 로그는 커밋되어 1건 발견
- 현상 : 부모의 RuntimeException이 자식의 트랜잭션에 영향 주지 않음

---
## 4. 핵심 해부
- 물리 트랜잭션의 분리 : REQUIRES_NEW는 현재 진행 중인 커넥션을 잠시 보류, 커넥션 풀에서 새로운 커넥션을 빌려 작업을 수행
- 독립된 커밋/롤백 : 자식 트랜잭션이 끝나는 시점에 이미 DB에 commit이 되기 때문에 이후 부모가 롤백되어도 이미 물리적으로 저장된 데이터는 롤백되지 않음

---
## 5. 주의 사항
- 커넥션 풀 고갈 위험 : REQUIRES_NEW는 한 요청에 최소 2개의 DB 커넥션을 점유하기 때문에 트래픽이 몰리면 커넥션 풀이 부족해져 서버가 먹통이 되는 데드락 상황이 올 수 있음
- 대안 : 단순 로그 저장이나 알림 서비스라면 REQUIRES_NEW 보단 비동기(@Async) 처리나 이벤트 기반 처리를 고려 하는게 안정성 측면에서 유리함.

---
## 6. CS Deep Dive
- 관련 개념 : 물리 트랜잭션 vs 논리 트랜잭션
- 핵심 요약 : 스프링은 하나의 물리적 커넥션 안에서 여러 논리 트랜잭션을 관리하지만 REQUIRES_NEW는 새로운 물리적 커넥션을 강제로 할당받아서 트랜잭션 간의 완전한 격리를 실현함.