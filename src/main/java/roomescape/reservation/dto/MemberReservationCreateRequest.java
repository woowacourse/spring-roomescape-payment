package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import roomescape.reservation.domain.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "사용자 예약 생성 dto")
public record MemberReservationCreateRequest(
        @Schema(description = "예약 날짜", example = "2024-06-07") @NotNull(message = "예약 날짜는 비어있을 수 없습니다.")
        LocalDate date,
        @Schema(description = "예약 시간 pk", example = "1") @NotNull(message = "예약 시간은 비어있을 수 없습니다.")
        Long timeId,
        @Schema(description = "테마 pk", example = "1") @NotNull(message = "테마는 비어있을 수 없습니다.")
        Long themeId,
        @Schema(description = "예약 상태", example = "CONFIRMATION") @NotNull(message = "예약 타입은 비어있을 수 없습니다.")
        ReservationStatus status,
        @Schema(description = "결제 키") String paymentKey,
        @Schema(description = "결제 id") String orderId,
        @Schema(description = "결제 금액", example = "21000") BigDecimal amount
) {
}
