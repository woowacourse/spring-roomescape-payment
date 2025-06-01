package roomescape.reservation.application.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import roomescape.reservation.model.vo.PaymentInfo;
import roomescape.reservation.model.vo.Schedule;

@Builder
public record CreateReservationServiceRequest(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Long amount
) {

    public Schedule toSchedule() {
        return Schedule.builder()
                .date(date)
                .timeId(timeId)
                .themeId(themeId)
                .build();
    }

    public PaymentInfo toPaymentInfo() {
        return PaymentInfo.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
