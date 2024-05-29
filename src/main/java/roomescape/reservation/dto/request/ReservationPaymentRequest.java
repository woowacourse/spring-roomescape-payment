package roomescape.reservation.dto.request;

import java.time.LocalDate;
import roomescape.auth.dto.LoginMember;

public record ReservationPaymentRequest(
        Long memberId,
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        Long amount
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
