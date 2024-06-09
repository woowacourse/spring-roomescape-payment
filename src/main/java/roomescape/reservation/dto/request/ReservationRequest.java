package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.payment.dto.request.PaymentRequest;

@Schema(name = "회원의 예약 저장 요청", description = "회원의 예약 요청시 사용됩니다.")
public record ReservationRequest(
        @NotNull(message = "예약 날짜는 null일 수 없습니다.")
        @Schema(description = "예약 날짜. 지난 날짜는 지정할 수 없으며, yyyy-MM-dd 형식으로 입력해야 합니다.", type = "string", example = "2022-12-31")
        LocalDate date,
        @NotNull(message = "예약 요청의 timeId는 null일 수 없습니다.")
        @Schema(description = "예약 시간 ID.", example = "1")
        Long timeId,
        @NotNull(message = "예약 요청의 themeId는 null일 수 없습니다.")
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "결제 위젯을 통해 받은 결제 키")
        String paymentKey,
        @Schema(description = "결제 위젯을 통해 받은 주문번호.")
        String orderId,
        @Schema(description = "결제 위젯을 통해 받은 결제 금액")
        Long amount,
        @Schema(description = "결제 타입", example = "NORMAL")
        String paymentType
) {

    @JsonIgnore
    public PaymentRequest getPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount, paymentType);
    }
}
