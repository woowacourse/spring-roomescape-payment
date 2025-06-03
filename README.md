# 🏠 룸이스케이프 예약 시스템

> **Spring Boot 기반 방탈출 테마 예약 관리 서비스**

---

## 📌 개요

**Roomescape Application**은 방탈출 테마에 대한 예약 기능을 제공하는 웹 서비스입니다.  
사용자는 테마를 조회하고 원하는 시간에 예약하거나 대기할 수 있으며, 로그인/회원가입 및 개인 예약 내역도 확인할 수 있습니다.  
**Spring Boot + Java + JPA** 기반으로 구현되었으며, 간결한 구조와 명확한 도메인 설계를 지향합니다.

---

## ✨ 주요 기능

### 🎨 1. 테마 관리
- ✅ 테마 생성 (관리자 전용)
- 📄 테마 목록 조회
- 🔝 인기 테마 조회
- ❌ 테마 삭제 (예약 없는 경우에 한해)

### 📅 2. 예약 관리
- ✍️ 예약 생성
- ⏳ 대기 예약 등록
- 📊 전체 예약 조회 (관리자 전용)
- 🙋 내 예약 조회
- 🗑 예약 취소 (대기 자동 승급 포함)

### ⏰ 3. 예약 시간 관리
- ➕ 시간대 생성 (관리자 전용)
- 🔍 전체 시간대 조회
- 📆 특정 테마의 가능 시간 조회
- 🚫 시간대 삭제 (예약 없는 경우에 한해)

### 👤 4. 사용자 기능
- 🔐 로그인 / 로그아웃
- 🆕 회원가입
- 📁 내 예약 내역 확인

---

## 🧩 도메인 모델

| 도메인 | 설명 | 주요 속성 | 관계 |
|--------|------|-----------|------|
| **Theme** | 방탈출 테마 | `id`, `name`, `description`, `thumbnail` | 예약과 연관 |
| **Reservation** | 사용자 예약 | `id`, `date`, `status`, `member`, `theme`, `reservationTime` | 테마, 시간, 회원 |
| **ReservationTime** | 예약 시간대 | `id`, `startAt` | 예약과 연관 |
| **Member** | 사용자 정보 | `id`, `email`, `name`, `password` | 예약과 연관 |

---

## 📡 API 목록

### 🎨 테마
```
POST   /themes            # 테마 생성
GET    /themes            # 전체 테마 조회
GET    /themes/ranking    # 인기 테마 조회
DELETE /themes/{id}       # 테마 삭제
```

### 📅 예약
```
POST   /reservations              # 예약 생성
POST   /reservations/waiting     # 대기 예약
GET    /reservations             # 전체 예약 조회
GET    /reservations/mine        # 내 예약 조회
GET    /reservations/waiting     # 대기 예약 목록 조회
DELETE /reservations/{id}        # 예약 취소
```

### 🛠 관리자 예약
```
POST /admin/reservations         # 관리자 예약 생성
GET  /admin/reservations         # 조건별 예약 조회
```

### ⏰ 예약 시간
```
POST   /times                    # 예약 시간 생성
GET    /times                    # 전체 시간 조회
GET    /times/available-time     # 테마/날짜별 가능 시간 조회
DELETE /times/{id}               # 시간 삭제
```

### 🧑‍💻 사용자 뷰
```
GET /                   # 메인 페이지
GET /reservation        # 예약 페이지
GET /login              # 로그인 페이지
GET /signup             # 회원가입 페이지
GET /reservation-mine   # 내 예약 페이지
```

---

## 📏 핵심 비즈니스 규칙

1. ✅ **테마 삭제 제한**: 예약이 걸린 테마는 삭제할 수 없습니다.
2. ⛔ **시간대 삭제 제한**: 예약이 걸린 시간은 삭제 불가입니다.
3. 🔁 **중복 예약 방지**: 동일한 테마/시간/날짜에는 한 번만 예약 가능.
4. 🔼 **대기 예약 자동 승급**: 예약 취소 시 대기 1순위가 자동 예약됩니다.

---

## 🧰 기술 스택

| 기술 | 설명 |
|------|------|
| **Java** | 백엔드 언어 |
| **Spring Boot** | 애플리케이션 프레임워크 |
| **JPA (Hibernate)** | ORM (객체-관계 매핑) |
| **Gradle** | 빌드 도구 |
| **Jakarta Validation** | 입력값 검증 |
| **Lombok** | 보일러플레이트 코드 제거 |

