package roomescape.application.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.application.payment.dto.PaymentClientRequest;

public record ReservationPaymentRequest(
        Long memberId,
        @NotNull(message = "테마 ID를 입력해주세요.")
        Long themeId,
        @NotNull(message = "날짜를 입력해주세요.")
        LocalDate date,
        @NotNull(message = "시간 ID를 입력해주세요.")
        Long timeId,
        @NotBlank(message = "결제 키를 입력해주세요.")
        String paymentKey,
        @NotBlank(message = "주문 ID를 입력해주세요.")
        String orderId) {

    public ReservationPaymentRequest withMemberId(long memberId) {
        return new ReservationPaymentRequest(memberId, themeId, date, timeId, paymentKey, orderId);
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(memberId, themeId, date, timeId);
    }

    public PaymentClientRequest toPaymentRequest(long price) {
        return new PaymentClientRequest(orderId, price, paymentKey);
    }
}
