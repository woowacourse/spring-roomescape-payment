package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.payment.domain.PaymentHistory;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.dto.CreateReservationServiceRequest;

public record CreateReservationWithPaymentWebRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        String paymentKey,
        String orderId,
        int amount
) {

    public PaymentConfirmRequest toPaymentConfirmRequest() {
        return new PaymentConfirmRequest(this.paymentKey, this.orderId, this.amount);
    }

    public CreateReservationServiceRequest toCreateServiceRequest(
            CreateReservationWithPaymentWebRequest webRequest,
            MemberInfo memberInfo
    ) {
        return new CreateReservationServiceRequest(memberInfo.id(), webRequest.date(), webRequest.timeId(),
                webRequest.themeId());
    }

    public PaymentHistory toPaymentHistory(Reservation reservation, PaymentStatus status) {
        return new PaymentHistory(orderId, amount, paymentKey, status, reservation);
    }
}
