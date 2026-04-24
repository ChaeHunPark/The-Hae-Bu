# 예외 발생 시 롤백 정책 (Checked vs Unchecked)

## 1. 개요
- 목표 : 모든 예외는 발생 시 트랜잭션을 롤백하는지 테스트
- 가설 : 예외의 종류에 따라 스프링의 롤백 대응 방식이 다를 것.

---
## 2. 코드

- 서비스 코드
```java
    @Transactional
    public void 언체크_예외_발생(String name) {
        유저_만들기("해부1",1000);
        throw new RuntimeException("이건 롤백 되나?");
    }

    @Transactional
    public void 체크_예외_발생(String name) throws Exception {
        유저_만들기("해부2", 1000);
        throw new Exception("이건 롤백 안된다던데?");
    }
```

- 테스트 코드
```java
    @Test
    void 실험4_체크_예외와_언_체크_예외의_롤백_테스트() {

        try {
            haebuService.언체크_예외_발생("언체크");
        } catch (Exception e) {
            System.out.println("에러 메시지: " + e.getMessage());
        }


        try {
            haebuService.체크_예외_발생("체크");
        } catch (Exception e) {
            System.out.println("에러 메시지: " + e.getMessage());
        }

        // 결과 확인
        System.out.println("--- DB 결과 확인 ---");
        userRepository.findAll().forEach(u -> System.out.println(u.getName()));
    }
```

- 테스트 결과
```java
Hibernate: insert into user (money,name) values (?,?)
에러 메시지: 이건 롤백 되나?
Hibernate: insert into user (money,name) values (?,?)
에러 메시지: 이건 롤백 안된다던데?
--- DB 결과 확인 ---
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0
해부2
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0
Hibernate: delete from user where id=?

```
---
## 3. 결과
- Uncheked Exception : insert는 나갔으나 DB 확인 결과 데이터 없음
- Checked Exception : insert가 나갔고 DB 확인결과 데이터 존재, commit 발생
- 결과 '해부2' 만 살아남음
---
## 4. 핵심 해부
- 스프링의 @Transactional은 기본적으로 RuntimeException과 Error만 롤백함
- 자바의 예외 설계 철학에 따라, Checked Exception은 개발자가 예외를 잡아서 복구할 수 있는 상황으로 간주함
---

## 5. 해결방법
- 비즈니스 로직상 Cheked Exception에도 롤백이 필요하면 rollbackFor 옵션 사용해야 함
---
## 6. CS Deep Dive
- 관련 개념 : 자바 예외 계층(Exception Hierarchy)
- 핵심 요약 : JVM은 예외를 '반드시 처리 해야하는 것'과 '프로그램 오류'로 구분, 스프링은 이 구분을 활용해 롤백 여부를 결정함