package roomescape.reservation.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ConfirmedReservationCreateWebRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") @Schema(description = "예약을 생성할 날짜") LocalDate date,
        @Schema(description = "예약할 예약시간의 id") Long timeId,
        @Schema(description = "예약할 테마의 id") Long themeId,
        @Schema(description = "예약 시 결제 승인에 사용할 결제 키") String paymentKey,
        @Schema(description = "예약 시 결제 승인에 사용할 주문번호") String orderId,
        @Schema(description = "예약 시 결제할 비용") Long amount
) {
    public ConfirmedReservationCreateWebRequest {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 반드시 입력해야합니다.");
        }
        if (timeId == null) {
            throw new IllegalArgumentException("timeId는 반드시 입력해야합니다.");
        }
        if (themeId == null) {
            throw new IllegalArgumentException("themeId는 반드시 입력해야합니다.");
        }
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("paymentKey는 반드시 입력해야합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId는 반드시 입력해야합니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount는 반드시 입력해야합니다.");
        }
    }
}
