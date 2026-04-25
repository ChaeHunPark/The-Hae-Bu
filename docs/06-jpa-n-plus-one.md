# JPA N+1 문제와 연관 관계의 함정

## 1. 개요
- 질문 : 유저 목록을 조회할 때, 연관된 게시글 데이터를 가져오는 과정에서 왜 수많은 쿼리가 발생하는가?
- 가설 : 지연 로딩으로 인해 프록시 객체가 실제 데이터가 필요한 시점에 개별적으로 쿼리를 날림, 성능 저하의 주범
---
## 2. 코드
- 서비스 코드
  - PostService
  ```java
    @Service
    public class PostService {
        @Autowired
        PostRepository postRepository;
        @Autowired
        UserRepository userRepository;
    
        @Transactional
        public void 글_쓰기(String title, User user) {
            Post post = new Post(title, user);
            postRepository.save(post);
        }
    
        @Transactional(readOnly = true)
        public void N플러스1_현상_재현() {
            System.out.println(" 모든 유저 조회 시작");
            List<User> users = userRepository.findAll();
    
            for(User user : users) {
                System.out.println("유저: " + user.getName() + "게시글 수: " + user.getPosts().size());
            }
        }
    }

  ```
  
- 테스트 코드
```java
    @BeforeEach
    void setup() {
        // 1. 유저 3명 생성
        User user1 = userRepository.save(new User("해부학자1", 1000));
        User user2 = userRepository.save(new User("해부학자2", 2000));
        User user3 = userRepository.save(new User("해부학자3", 3000));

        // 2. 각 유저당 게시글 2개씩 생성
        postRepository.save(new Post("글1-1", user1));
        postRepository.save(new Post("글1-2", user1));

        postRepository.save(new Post("글2-1", user2));
        postRepository.save(new Post("글2-2", user2));

        postRepository.save(new Post("글3-1", user3));
        postRepository.save(new Post("글3-2", user3));

        // [중요] 영속성 컨텍스트 비우기
        // 이걸 안 하면 1차 캐시에 데이터가 남아서 쿼리가 안 나갈 수 있음
        em.flush();
        em.clear();
        System.out.println("--- 데이터 준비 완료 및 영속성 컨텍스트 초기화 ---");
    }

    @Test
    @DisplayName("N+1 문제를 눈으로 확인하는 테스트")
    void nPlusOneVisualTest() {
        System.out.println("=== 1. 모든 유저 조회 (쿼리 1번 예상) ===");
        List<User> users = userRepository.findAll();
        System.out.println("실제 조회된 유저 수: " + users.size());

        System.out.println("=== 2. 각 유저의 게시글에 접근 (추가 쿼리 N번 예상) ===");
        for (User user : users) {
            // 이 시점에 프록시 객체가 초기화되면서 DB에 쿼리를 날림
            System.out.println("유저명: " + user.getName() + " | 게시글 수: " + user.getPosts().size());
        }
    }
```
- 테스트 결과
```java
=== 1. 모든 유저 조회 (쿼리 1번 예상) ===
Hibernate: select u1_0.id,u1_0.money,u1_0.name from user u1_0
실제 조회된 유저 수: 3
=== 2. 각 유저의 게시글에 접근 (추가 쿼리 N번 예상) ===
Hibernate: select p1_0.user_id,p1_0.id,p1_0.title from post p1_0 where p1_0.user_id=?
유저명: 해부학자1 | 게시글 수: 2
Hibernate: select p1_0.user_id,p1_0.id,p1_0.title from post p1_0 where p1_0.user_id=?
유저명: 해부학자2 | 게시글 수: 2
Hibernate: select p1_0.user_id,p1_0.id,p1_0.title from post p1_0 where p1_0.user_id=?
유저명: 해부학자3 | 게시글 수: 2
```
---
## 3. 결과
- 상황 : 유저 3명이 게시글 2개씩 저장
- 실행 : findAll() 호출 후 user.getPosts().size()로 접근
- 결과 : 유저 1명당 1번의 추가 쿼리 발생
  - 쿼리 갯수 : 전체 유저 1번, 유저 3명의 쿼리 3번, 총 4번의 쿼리 발생
  - 유저가 10000명이면 10001번의 DB 호출
---
## 4. 핵심 해부
- 지연 로딩의 배신
  - JPA는 효율성을 위해 연관 객체를 가짜 객체인 프록시로 채워둠
  - 하지만 루프를 돌며 필드에 접근하는 순간 각 객체는 자신의 데이터를 찾기 위해 개별적으로 DB한테 갖고오라고 쿼리 날림
- JPQL의 단순성
  - findAll()은 오직 해당 엔티티만을 대상으로 쿼리를 생성함
  - 연관된 엔티티가 있는지 모름
  - 나중에 부족한 데이터를 채우기 위해서 뒷북 쿼리가 터짐
---
## 5. 해결 방법
- join fetch 키워드를 사용하여 단 한 번의 INNER JOIN 쿼리로 유저와 게시글을 통째로 가져옴
  - join fetch : 조인한 결과를 객체 그래프에 다 떄려 넣어라
- @Query 어노테이션으로 수동 명시
```java
    @Query("select u from User u join fetch u.posts")
    List<User> findAllWithPostsFetchJoin();
```

```java
    @Test
    @DisplayName("Fetch Join으로 N+1 문제를 해결하는 테스트")
    void fetchJoinTest() {
        System.out.println("=== Fetch Join 실행 (쿼리 딱 1번 예상) ===");
        List<User> users = userRepository.findAllWithPostsFetchJoin();

        for (User user : users) {
            System.out.println("유저명: " + user.getName() + " | 게시글 수: " + user.getPosts().size());
        }
    }
```

- 테스트 결과
```java
=== Fetch Join 실행 (쿼리 딱 1번 예상) ===
Hibernate: select u1_0.id,u1_0.money,u1_0.name,p1_0.user_id,p1_0.id,p1_0.title from user u1_0 join post p1_0 on u1_0.id=p1_0.user_id
유저명: 해부학자1 | 게시글 수: 2
유저명: 해부학자2 | 게시글 수: 2
유저명: 해부학자3 | 게시글 수: 2
```
## 5.2 추가 해결 방법
- @BatchSize
  - 문제: Fetch Join은 쿼리 한 번으로 해결되지만 DB 레벨의 페이징 Limit, Offset이 불가능함
  - 해결: yml에 전역 설정을 추가하거나 @BatchSize 적용
  ```yaml
    spring:
      jpa:
        properties:
          hibernate:
            default_batch_fetch_size: 100 # 보통 100~1000 사이 설정
  ```
  - 결과: 100개를 조회할 때 원래 101번 나갈 쿼리가 1 + 1번으로 줄어듬 
  - 연관된 객체를 조회할 때 IN 절을 사용하여 설정한 사이즈만큼 한번에 긁어옴 
  - 예시: 배치 사이즈가 10인 경우 나가는 쿼리
    - ```java
          SELECT * FROM post WHERE user_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);  
      ```
    - JPA는 1번 유저 게시글이 필요하네? 근데 배치 사이즈가 10이네?
    - 그럼 영속성 컨텍스트에 있는 다른 유저 9명의 ID도 같이 모아서 쿼리 날려야지ㅎ
    - 1번의 쿼리로 유저 10명의 게시글이 메모리에 다 올라옴
    - 2~10번 유저는 이미 메모리에 있어서 쿼리 안나감
    - Fetch Join은 1번의 쿼리가 나가는 대신 페이징 처리가 메모리 가부하 위험 때문에 불가능하며 카테시안 곱이 발생 할수 있음, 레포지토리마다 쿼리 작성해야하는 불편함이 있음
    - BatchSize는 1 + 전체/BatchSize번 쿼리가 나감 이말은 즉슨 페이징 처리에 매우 유리하고 데이터 중복은 없으며 yml 설정 한 줄로 전역으로 적용 가능함
    - 극한의 성능은 fetch join, BatchSize는 페이징 처리할 때 좋음, 각각 상황에 맞게 선택하면 될듯

  
## 5.3 주의사항: 카테시안 곱(Cartesian Product)
- 현상: 1:N 조인하면 결과 행이 N의 개수만큼 불어남 유저 1당 게시글 5개면 결과는 5줄
- JPA는 하이버네이트 6버전부터 중복된 엔티티를 자동으로 제거해줌
- 이전에는 select distinct u 처럼 distinct 키워드가 필요할 수 있음
  - 중요: 두 개 이상의 Collection을 동시에 Fetch Join 하면 안됨
    - MultipleBagFetchException 발생 위험
    - 예시
    ```java
    @Entity
    public class Order {
        @Id @GeneratedValue
        private Long id;
    
        // 첫 번째 컬렉션: 주문한 상품들 (OrderItems)
        @OneToMany(mappedBy = "order")
        private List<OrderItem> orderItems = new ArrayList<>();
    
        // 두 번째 컬렉션: 배송 추적 로그 (DeliveryLogs)
        @OneToMany(mappedBy = "order")
        private List<DeliveryLog> deliveryLogs = new ArrayList<>();
    }
    ```
    - 주문 1개에 상품 5개 배송 로그가 10개면 DB 결과는 50줄이 됨 각각 카테시안 곱으로 DB 결과가 50줄이 됨
    - 데이터가 중복되면서 JPA가 객체로 변환할 때 어떤 데이터가 진짜인지 판단하기 어려워짐


    ### 1:N관계에서 FetchJoin은 하나만, 나머지는 @BatchSize에게 맡김 ###

## 6. @Query의 장단점
- 장점 : N+1 문제 깔끔하게 해결, 쿼리 수 줄임
- 단점
  - 쿼리가 문자열이라 컴파일 시점에 알기 어려움 (Querydsl이 필요한 이유)
  - 페이징 처리를 하면 위험함, 메모리 가부하 문제

## 7. CS Deep Dive
- 개념: 객체 그래프 탐색(Object Graph Traversal)
- 내용: 객체는 점(Node)이고 연관 관계는 선(Edge)이다. @Query의 Fetch join은 이 선을 따라가며 연결된 모든 점을 한 번에 메모리로 끌어올리는 행위