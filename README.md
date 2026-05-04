# 🔪 the-hae-bu (더 해부)
> **"Understand through destruction."** > 돌아가는 코드를 복사하지 않는다. 직접 고장 내고 해부하며 본질을 조진다.

## 🔬 실험 철학 (Experimental Philosophy)
많은 이들이 AI가 짜준 코드를 그대로 사용하지만, 나는 그 코드가 **'왜'** 돌아가는지, 그리고 **'언제'** 부서지는지 알고 싶다.  
`the-hae-bu`는 기술을 극단적인 상황으로 몰아넣고, 그 한계를 기록하는 엔지니어링 일지다.

### 🧪 실험 프로세스 (3-Step Subtraction)
1. **[실험]**: 특정 어노테이션이나 설정을 의도적으로 제거하거나 비틀어본다.
2. **[느낌]**: 시스템이 어떻게 무너지는지 관찰하고, 나만의 가설을 세운다.
3. **[정석]**: 공식 문서와 내부 소스코드를 까보며 '진짜 원인'을 해부한다.

---

## 📂 실험 리스트 (The Log)
### 🔬 백엔드 해부학자: 100인의 마스터 커리큘럼 (실험 현황판)

| 순번     | 실험 주제 (Click to Report)                                                         | 핵심 키워드                                             | 상태 |
|:-------|:--------------------------------------------------------------------------------|:---------------------------------------------------| :--- |
| **01** | **[@Transactional을 빼면 어떻게 될까?](./docs/01-transactional-off.md)**                | AOP, Proxy, Dirty Checking                         | ✅ 집도 완료 |
| **02** | **[@Transactional(readOnly = true)의 실체](./docs/02-transactional-read-only.md)** | Snapshot, Connection Optimization                  | ✅ 집도 완료 |
| **03** | **[프록시 자기 호출(Self-Invocation)의 배신](./docs/03-proxy-self-invocation.md)**        | Internal Call, AspectJ, Bean Separation            | ✅ 집도 완료 |
| **04** | **[예외 발생 시 롤백 정책 (Checked vs Unchecked)](./docs/04-exception-rollback.md)**     | RuntimeException, RollbackFor, Exception Hierarchy | ✅ 집도 완료 |
| **05** | **[트랜잭션 전파(REQUIRES_NEW)의 생존 전략](./docs/05-propagation-requires-new.md)**       | Physical/Logical TX, HikariCP Deadlock             | ✅ 집도 완료 |
| **06** | **[JPA N+1 문제: 연관 관계의 함정](./docs/06-jpa-n-plus-one.md)**                        | Fetch Join, EntityGraph, BatchSize                 | ✅ 집도 완료 |
| **07** | **[영속성 컨텍스트: 1차 캐시와 동일성 보장](./docs/07-persistence-context.md)**                 | Persistence Context, Identity, Dirty Checking      | ✅ 집도 완료 |
| **08** | **[변경 감지(Dirty Checking): 업데이트의 비밀](./docs/08-dirty-checking-snap-shot.md)** 	                                     | Snapshot, Flush, Managed Entity                    |	✅ 집도 완료|
---

## 🛠️ Stack & Infrastructure
- **Language:** Java 17
- **Framework:** Spring Boot 3.4
- **Database:** MySQL 8.0 (Docker-compose 기반)
- **Testing:** JUnit5, AssertJ

---
