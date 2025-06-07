package roomescape.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.PaymentConfirmRequest;

@Schema(description = "예약 생성 요청 DTO")
public record CreateReservationRequest(
        @Schema(description = "예약 날짜", example = "2026-10-01")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        Long themeId,

        @Schema(description = "시간 ID", example = "1")
        Long timeId,

        @Schema(description = "결제 타입", example = "NORMAL")
        PaymentType paymentType,

        @Schema(description = "결제 정보")
        PaymentConfirmRequest paymentRequest
) {
}
