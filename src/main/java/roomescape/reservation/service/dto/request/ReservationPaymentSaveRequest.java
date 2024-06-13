package roomescape.reservation.service.dto.request;

import java.time.LocalDate;
import roomescape.auth.dto.LoginMember;

public record ReservationPaymentSaveRequest(
        long memberId,
        LocalDate date,
        long themeId,
        long timeId,
        String paymentKey,
        String orderId,
        long amount
) {
    public static ReservationPaymentSaveRequest of(ReservationPaymentSaveRequest saveRequest, LoginMember loginMember) {
        return new ReservationPaymentSaveRequest(
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
