package roomescape.service.payment.dto;

import roomescape.domain.payment.PaymentInfo;
import roomescape.domain.payment.PaymentMethod;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.reservation.Reservation;

public record PaymentConfirmOutput(
        String paymentKey,
        PaymentType type,
        String orderId,
        String orderName,
        String currency,
        String method,
        Long totalAmount,
        PaymentStatus status) {
    public ReservationPayment toReservationPayment(Reservation reservation) {
        PaymentMethod findMethod = PaymentMethod.findByDescription(method);
        PaymentInfo info = new PaymentInfo(
                paymentKey, type, orderId, orderName, currency, findMethod, totalAmount, status);
        return new ReservationPayment(info, reservation); // TODO: 도메인 간 embeddable 생성 방식 통일하기
    }
}
