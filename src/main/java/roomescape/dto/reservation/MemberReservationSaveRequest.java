package roomescape.dto.reservation;

import roomescape.dto.MemberResponse;
import roomescape.dto.payment.PaymentRequest;

import java.time.LocalDate;

public record MemberReservationSaveRequest(
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Integer amount
) {

    public ReservationSaveRequest generateReservationSaveRequest(MemberResponse memberResponse) {
        return new ReservationSaveRequest(memberResponse.id(), date, timeId, themeId, null, amount);
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(orderId, amount, paymentKey);
    }
}
