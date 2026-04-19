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

| 순번 | 실험 주제 (Click to Report)                                          | 핵심 키워드 | 상태 |
| :--- |:-----------------------------------------------------------------| :--- | :--- |
| **01** | **[@Transactional을 빼면 어떻게 될까?](./docs/01-transactional-off.md)** | AOP, Proxy, Atomicity | 🏗️ 집도 중 |

---

## 🛠️ Stack & Infrastructure
- **Language:** Java 17
- **Framework:** Spring Boot 3.4
- **Database:** MySQL 8.0 (Docker-compose 기반)
- **Testing:** JUnit5, AssertJ

---
