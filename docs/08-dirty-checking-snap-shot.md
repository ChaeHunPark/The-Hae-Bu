### 변경 감지(Dirty Checking)와 스냅샷

## 1. 개요
- 질문 : 엔티티의 필듣 값만 바꿔도 왜 JPA는 update 쿼리를 알아서 날리는가?
- 가설 : JPA는 영속성 컨텍스트에 엔티티를 보관할 때 최초 상태를 복사(스냅샷)해놓고, 이를 현재 상태와 비교하여 변경 사항을 자동으로 반영할 것.

## 2. 코드
- 테스트 코드
```java
@Test
    @DisplayName("변경 감지(Dirty Checking) 실험")
    void dirtyCheckingTest() {
        // 영속성 컨텍스트 올리기
        User user = userRepository.save(new User("해부8",8000));

        // 영속성 컨텍스트 비우고 조회로 영속 상태 만들기
        em.flush();
        em.clear();
        User foundUser = userRepository.findById(user.getId()).get();

        System.out.println("1. 데이터 수정하기");
        foundUser.이름_수정("해부9");

        System.out.println("2. 트랜잭션 종료 직전 (flush 발생 예상)");
    }
```
- 테스트 결과
```java
Hibernate: insert into user (money,name) values (?,?)
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0 where u1_0.id=?
1. 데이터 수정하기
2. 트랜잭션 종료 직전 (flush 발생 예상)
Hibernate: update user set money=?,name=? where id=?
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0
```

## 3. 결과
- 로그
  1. insert : 최초 데이터 저장
  2. select : 수정을 위해 데이터를 영속성 컨텍스트로 불러옴(스냅샷 생성)
  3. update : 트랜잭션이 끝나는 시점에 스냅샷과 현재 엔티티를 비교하여 변경도힌 부분에 대해 쿼리 실행

## 4. 핵심 해부
- 스냅샷(Snapshot) : JPA는 엔티티를 영속성 컨텍스트에 보관할 때, 최초 상태를 복사해서 저장해둠
- 플러시(Flush) 시점
  1. 트랜잭션 커밋되거나 em.flush()가 호출되면 엔티티와 스냅샷을 비교
  2. 변경된 내용이 있다면 update 쿼리를 날려서 쓰기 지연 SQL 저장소에 보냄
  3. 쓰기지연 저장소의 쿼리를 DB에 전송
- 영속 상태 필수 : 영속성 컨텍스트가 관리하는 상태인 준영속, 비영속 상태이면 아무리 값을 바꿔도 JPA가 감지하지 못함
- 트랜잭션 범위 : @Transactinal이 붙어있어야 메서드 종료 시점에 자동으로 flush와 commit이 일어나며 변경 감지가 작동

## 5. CS Deep Dive : 업데이트 쿼리의 범위
- 기본 전략 : JPA는 변경된 필드만 수정하는 것이 아니라 엔티티의 모든 필드를 업데이트하는 쿼리를 생성
  - 이유 : 쿼리가 항상 일정하므로 DB 입장에서 쿼리 재사용(파싱되니 쿼리 재활용이) 가능해서
  - 최적화 : 필드가 한 30개? 정도 되어서(그럴리는 거의 없지만) 성능이 걱정되면 ***@DynamicUpdate***를 사용하여 바뀐 필드만 동적으로 쿼리 생성할 수 있음
