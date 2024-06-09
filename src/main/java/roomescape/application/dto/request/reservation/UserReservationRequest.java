package roomescape.application.dto.request.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.application.dto.request.payment.PaymentRequest;

@Schema(name = "사용자 예약 정보")
public record UserReservationRequest(
        @Schema(description = "예약 날짜", example = "2024-10-10")
        @Future(message = "과거에 대한 예약은 할 수 없습니다.")
        @NotNull(message = "날짜에 빈값은 허용하지 않습니다.")
        LocalDate date,

        @Schema(description = "멤버 ID", example = "4")
        @Positive(message = "타임 아이디는 1이상의 정수만 허용합니다.")
        Long timeId,

        @Schema(description = "테마 ID", example = "1")
        @Positive(message = "테마 아이디는 1이상의 정수만 허용합니다.")
        Long themeId,

        @Schema(description = "결제 금액", example = "10000")
        Long amount,

        @Schema(description = "주문 ID", example = "1")
        String orderId,

        @Schema(description = "결제 키", example = "imp_1234567890")
        String paymentKey,

        @Schema(description = "결제 타입", example = "toss")
        String paymentType
) {

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(
                amount,
                orderId,
                paymentKey
        );
    }
}
