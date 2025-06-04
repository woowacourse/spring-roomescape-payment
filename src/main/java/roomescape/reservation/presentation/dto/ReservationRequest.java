package roomescape.reservation.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(
    @NotNull(message = "날짜는 null 일 수 없습니다.") LocalDate date,
    @NotNull(message = "예약 시간 번호는 null 일 수 없습니다.") Long timeId,
    @NotNull(message = "테마 번호는 null 일 수 없습니다.") Long themeId,
    @NotNull(message = "페이먼트키는 null 일 수 없습니다.") String paymentKey,
    @NotNull(message = "주문 번호는 null 일 수 없습니다.") String orderId,
    @NotNull(message = "금액은 null 일 수 없습니다.") int amount) {
    public ReservationRequest {

    }
}
