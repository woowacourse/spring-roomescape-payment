package roomescape.reservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import roomescape.payment.dto.PaymentConfirmRequest;

public record MemberReservationWithPaymentAddRequest(
        @NotNull(message = "예약 날짜는 필수 입니다.") LocalDate date,
        @NotNull(message = "예약 시간 선택은 필수 입니다.") @Positive Long timeId,
        @NotNull(message = "테마 선택은 필수 입니다.") @Positive Long themeId,
        @NotNull(message = "결제키는 필수 입니다.") String paymentKey,
        @NotNull(message = "주문 ID는 필수 입니다.") String orderId,
        @NotNull(message = "결제 금액은 필수 입니다.") @Positive Long amount
) {

    public PaymentConfirmRequest extractPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }

    public MemberReservationAddRequest extractMemberReservationAddRequest() {
        return new MemberReservationAddRequest(date, timeId, themeId);
    }
}
