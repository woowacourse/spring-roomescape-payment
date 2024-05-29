package roomescape.application.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.application.payment.dto.request.PaymentRequest;

public record ReservationPaymentRequest(
        Long memberId,
        @NotNull(message = "날짜를 입력해주세요.")
        LocalDate date,
        @NotNull(message = "시간 ID를 입력해주세요.")
        Long timeId,
        @NotNull(message = "테마 ID를 입력해주세요.")
        Long themeId,
        @NotBlank(message = "결제 키를 입력해주세요.")
        String paymentKey,
        @NotBlank(message = "주문 ID를 입력해주세요.")
        String orderId) {

    public ReservationPaymentRequest(Long memberId, LocalDate date, Long timeId, Long themeId) {
        this(memberId, date, timeId, themeId, null, null);
    }

    public ReservationPaymentRequest withMemberId(long memberId) {
        return new ReservationPaymentRequest(memberId, date, timeId, themeId, paymentKey, orderId);
    }

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(memberId, date, timeId, themeId);
    }

    public PaymentRequest toPaymentRequest(long price) {
        return new PaymentRequest(orderId, price, paymentKey);
    }
}
