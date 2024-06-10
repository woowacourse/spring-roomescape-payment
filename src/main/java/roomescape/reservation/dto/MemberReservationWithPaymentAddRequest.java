package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.payment.dto.PaymentConfirmRequest;

public record MemberReservationWithPaymentAddRequest(

        @Schema(description = "예약 날짜", example = "2099-12-31")
        @NotNull(message = "예약 날짜는 필수 입니다.")
        LocalDate date,

        @Schema(description = "예약 시간 id", example = "1")
        @NotNull(message = "예약 시간 선택은 필수 입니다.")
        @Positive
        Long timeId,

        @Schema(description = "예약 테마 id", example = "1")
        @NotNull(message = "테마 선택은 필수 입니다.")
        @Positive
        Long themeId,

        @Schema(description = "PG의 결제 키", example = "test_xxxxxxxxxx")
        @NotNull(message = "결제키는 필수 입니다.")
        String paymentKey,

        @Schema(description = "PG의 주문 id", example = "ROOMESCAPE_xxxxxxxxxx")
        @NotNull(message = "주문 ID는 필수 입니다.")
        String orderId,

        @Schema(description = "설정한 상품의 결제 금액", example = "20000")
        @NotNull(message = "결제 금액은 필수 입니다.")
        @Positive
        Long amount
) {

    public PaymentConfirmRequest extractPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }

    public MemberReservationAddRequest extractMemberReservationAddRequest() {
        return new MemberReservationAddRequest(date, timeId, themeId);
    }
}
