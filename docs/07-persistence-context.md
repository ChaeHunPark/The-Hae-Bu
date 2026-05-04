# 영속성 컨텍스트: 1차 캐시와 동일성 보장

## 1. 개요
- 질문 : 같은 트랜잭션 안에서 똑같은 데이터를 두 번 조회하면 DB는 두 번 일해야 할까?
- 가설 : JPA의 영속성 컨텍스트(1차 캐시)가 중간에서 방어막 역할을 하며, 두 번째 조회 부터는 DB 접근 없이 메모리에서 해결함

## 2. 코드
- 테스트 코드
  ```java
    @Test
    @DisplayName("1차 캐시와 동일성 보장 실험")
    void persistenceTest() {
        // 유저  한 명 저장
        User user = new User("해부7", 7000);
        userRepository.save(user);

        // 영속성 컨텍스트를 DB 동기화하고 비우기
        em.flush();
        em.clear();

        System.out.println(" 1. 첫 번째 조회 (DB 쿼리 날아감");
        User firstUser = userRepository.findById(user.getId()).get();

        System.out.println(" 2. 두 번째 조회 (쿼리 안날아감)");
        User secondUser = userRepository.findById(user.getId()).get();

        System.out.println(" 3. 동일성 비교");
        System.out.println(" firstUser == secondUser : " + (firstUser == secondUser));

    }
  ```
  
- 테스트 결과
```java
Hibernate: insert into user (money,name) values (?,?)
 1. 첫 번째 조회 (DB 쿼리 날아감
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0 where u1_0.id=?
 2. 두 번째 조회 (쿼리 안날아감)
 3. 동일성 비교
 firstUser == secondUser : true
```

## 3. 결과
- 쿼리 발생 횟수: 1번
- 이유:  1차 조회 시 DB에서 갸져온 데이터를 1차 캐시에 저장, 2차 조회 시 캐시에서 바로 반환함
- 객체 비교 : user1 == user2 는 true, JPA가 같은 컨텍스트 내에서 인스턴스의 동일성 보장

## 4. 핵심 해부
- 영속성 컨텍스트(Persistence Context): 엔티티를 영구 저장하는 환경임 애플리케이션과 DB 사이의 중간 계층
- 1차 캐시(First-level Cache): 영속성 컨텍스트 내부에 존재하는 보관함(Key: @Id, Value: 엔티티 인스턴스)
- 동일성(Identity) 보장: 같은 영속성 컨텍스트 안에서 조회한 엔티티는 주소값이 같다(== 비교 시 true)

## 5. CS Deep Dive
- 성능 최적화: 불필요한 DB 호출을 줄여서 네트워크 비용 절감
- 편의성 : DB에서 꺼내온 데이터라도 자바 컬렉션처럼 동일성을 믿고 == 비교를 사용할 수 있음