package roomescape.reservation.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 상태를 나타냅니다.", allowableValues = {"CONFIRMED", "CONFIRMED_PAYMENT_REQUIRED", "WAITING"})
public enum ReservationStatus {
    @Schema(description = "결제가 완료된 예약")
    CONFIRMED,
    @Schema(description = "결제가 필요한 예약")
    CONFIRMED_PAYMENT_REQUIRED,
    @Schema(description = "대기 중인 예약")
    WAITING;
}
