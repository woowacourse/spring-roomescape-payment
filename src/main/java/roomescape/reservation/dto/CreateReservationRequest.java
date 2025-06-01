package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.payment.processor.PaymentConfirmRequest;
import roomescape.payment.processor.PaymentType;

public record CreateReservationRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        PaymentType paymentType,
        PaymentConfirmRequest paymentRequest
) {
}
