
> 
>
# 🔬 @Transactional(readOnly = true)의 실체

## 1. 개요
* **목표:** `@Transactional(readOnly = true)` 설정 시 실제로 DB 수정이 차단되는지 확인하고, 매커니즘 이해하기
* **가설:** 읽기 전용 모드에서는 JPA의 스냅샷 생략으로 객체 값을 바꿔도 UPDATE 쿼리가 발생하지 않을 것.

---

## 2. 1차 시도 (예상 실패)

- 서비스 코드

```java
@Transactional(readOnly = true)
public void 돈_채우기(String name, int 돈) {
    User user = userRepository.findByName(name);
    user.돈_채우기(돈); // 10,000 -> 15,000 변경 시도
}
```

- 테스트 코드
```java
    @Test
    @Transactional
    void 실험2_readOnly_true는_디비에_반영될까() {

        haebuService.유저_만들기("해부11", 10000);
        haebuService.돈_채우기("해부11",5000);

        User user = userRepository.findByName("해부11");

        System.out.println(user.돈_확인하기());
    }

```

* **상황:** 서비스 코드에는 `readOnly = true`를 붙였으나, 테스트 코드에도 `@Transactional`이 붙어 있는 상태.
* **결과:** DB 값이 변경됨 (업데이트 발생).
* **원인 분석:**
    * **범인:** 테스트 메서드에 붙어 있던 `@Transactional`.
    * **매커니즘:** 스프링 **트랜잭션 전파(Propagation)** 특성 때문에 테스트의 트랜잭션이 서비스로 전파되었고, `readOnly = true` 옵션이 무시됨. 부모의 성질을 물려받아 Dirty Checking이 발생함.
    * **해결 방법:** 테스트에서 `@Transactional`을 제거하고, `@AfterEach` 등으로 데이터를 수동 정리함.

---

## 3. 2차 시도 (실험 성공)

3. 2차 시도 : 테스트 코드에서 @Transactional 빼기
```java
Hibernate: select ... from user where name=? -- 조회 쿼리
Hibernate: select ... from user where name=? -- 검증용 조회 쿼리
-- UPDATE 쿼리가 감쪽같이 사라짐 (결과: 10,000원 유지)
```


* **상황:** 테스트 코드에서 `@Transactional`을 제거하고 실행.
* **결과:** UPDATE 쿼리가 발생하지 않음 (기존 데이터 유지).

---

## 4. 핵심 해부

### 💡 업데이트가 나가지 않는 이유 (JPA 최적화)
* **스냅샷 생략:** `readOnly = true`를 감지한 JPA는 영속성 컨텍스트에 올릴 때 스냅샷을 만들지 않음.
* **변경 감지 불가:** 트랜잭션 종료 시점에 비교할 원본이 없으므로 수정 사항을 감지하지 않고 UPDATE 쿼리도 생성하지 않음.

### ❓ readOnly = true를 왜 써야 할까?
* **메모리 절약:** 스냅샷을 보관하지 않으므로 영속성 컨텍스트의 메모리 사용량이 줄어듦.
* **CPU 절약:** 트랜잭션 종료 시 객체를 일일이 비교하는 연산을 생략할 수 있음.
* **DB 최적화:** DB 엔진에 따라 읽기 전용 트랜잭션 최적화(락 관리 등)가 적용됨.
* **안전성:** 조회 전용 메서드에서 실수로 데이터를 수정하는 사이드 이펙트 방지.

---

## 5. 결론 및 느낀점
> 단순히 서비스 코드에 옵션 적는 거보다 실제로 어떤 트랜잭션 환경에서 실행되는지 파악하는 데 도움이 되었음. 테스트 코드의 `@Transactional`은 편리하지만, 실제 로직과 혼선을 줄 수 있다는 것을 깨달음.