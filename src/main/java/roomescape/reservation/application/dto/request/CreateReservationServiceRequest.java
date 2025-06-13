package roomescape.reservation.application.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentStatus;
import roomescape.payment.model.PaymentType;
import roomescape.reservation.model.vo.Schedule;

@Builder
public record CreateReservationServiceRequest(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        int amount
) {

    public Schedule toSchedule() {
        return Schedule.builder()
                .date(date)
                .timeId(timeId)
                .themeId(themeId)
                .build();
    }

    public Payment toPayment(Long reservationId, PaymentStatus paymentStatus, PaymentType paymentType) {
        return Payment.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .reservationId(reservationId)
                .status(paymentStatus)
                .paymentType(paymentType)
                .build();
    }
}
