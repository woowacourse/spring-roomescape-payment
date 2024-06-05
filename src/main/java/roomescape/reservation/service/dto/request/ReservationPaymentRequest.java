package roomescape.reservation.service.dto.request;

import java.time.LocalDate;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.controller.dto.request.ReservationPaymentSaveRequest;

public record ReservationPaymentRequest(
        long memberId,
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount
) {
    public static ReservationPaymentRequest of(ReservationPaymentSaveRequest saveRequest, LoginMember loginMember) {
        return new ReservationPaymentRequest(
                loginMember.id(),
                saveRequest.date(),
                saveRequest.themeId(),
                saveRequest.timeId(),
                saveRequest.paymentKey(),
                saveRequest.orderId(),
                saveRequest.amount()
        );
    }
}
