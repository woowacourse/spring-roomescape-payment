### Step2 (결제 도입) 설계

#### 1. 요구사항

- **Response 설계**: 자유롭게 할 수 있음.
- 자신만의 기준을 세워 **DB를 설계**하고 **기능을 구현**.
- 설계 시 고민해볼 만한 질문:
    1. **Toss 결제 승인 API 응답의 필드 중 어떤 데이터를 저장할 것인가? 그 이유는 무엇인가?**
        - **우리 서버에 저장해야 하는 필드**:
            - **Id**: 결제 구별을 위한 PK.
            - **paymentKey**: 결제 정보 전체를 조회 가능.
            - **amount**: 토스 API 이상 발생 시 환불 요청에 필요.
            - **orderId**:
                - **왜 필요한가?**
                    - `paymentKey`: 결제 인증 완료 후 토스에서 발급한 식별자.
                    - `orderId`: 상점에서 미리 발급한 주문 식별자로, 우리 서버에서 주문 관리를 위해 필요.

            - **status**: 결제 취소 등 상태 저장.

    2. **결제 정보를 새로운 테이블에 저장할 것인가, 예약에 컬럼만 추가할 것인가?**
        - **테이블로 분리** 추천:
            - 예약과 결제는 독립적인 도메인.
            - 대기 중인 예약 또는 미결제 예약은 결제가 필요하지 않음.
            - 하나의 컬럼에 값이 없을 경우 관리 복잡.

    3. **예약과 결제를 분리한다면 그 이유와, 둘의 관계는?**
        - **ManyToOne 관계로 설정**:
            - 결제가 예약 ID를 참조하는 구조.
            - 결제 유무는 예약의 관심사가 아니므로 결제가 연관관계의 주인 역할을 담당.

#### 2. 내 예약 불러오는 플로우

- **기존 방식**: `userName`으로 모든 조회. `getMyReservations``ReservationWithAheadDto`
- **변경안**:
    - [ ] **Payment 테이블에서 userName으로 조회**
        - **문제점**: 내 예약을 불러오는데 PaymentService 접근 → 부자연스러움.
        - **대안**:
            - **결제된 예약**은 PaymentService에서,
            - **미결제 예약**은 Reservation에서 조회.
    - [x] **LEFT JOIN으로 한 번에 조회**:
      - JPQL 쿼리 (결제 정보 없는 경우에도 조회 가능):
      - 예약 레포지토리를 활용하여 결제 정보 여부와 관계없이 한 번에 조회.

``` java
       SELECT r, p 
       FROM Reservation r 
       LEFT JOIN Payment p ON p.reservation = r
```

- 반환 DTO: `ReservationPaymentDto`.
    - **결제 정보 있음** → 해당 필드가 채워짐.
    - **결제 정보 없음** → 해당 필드가 `null`.

#### 3. 기타 변경안

1. **userName으로 ReservationWithAheadDto 조회 후 Payment 조회:**
    - DB IO가 예약 개수만큼 발생.
    - **대안**: 페이징으로 각 요청당 최대 쿼리 수 제한 가능.

2. **Payment에 userName 필드를 추가 후 모든 예약 및 대기를 조회 및 저장:**
    - 기존과 같은 논리 유지 가능.

### 로깅

- 어디에 찍을까?
    - [x] **외부 API 호출 전후.**
        - Spring AOP 활용
    - [x] **권한 인증/인가 과정.**
        - ControllerAdvice에 직접 로깅
    - [ ] 주요 비즈니스 로직 처리.
    - [ ] 애플리케이션 시작 및 종료.
    - [ ] DB작업

### API 문서

- [배포 링크](https://app.swaggerhub.com/apis/woowacourse-dd6/spring-roomescape_payment_api/1.0.0)
    - openAPI 명세 yaml 파일 생성 : **IntelliJ EndPoint 자동생성 활용**
    - yaml → SwaggerUI 문서 작성 및 배포 : **swagger Hub 이용**

### ERD

![img.png](img.png)
