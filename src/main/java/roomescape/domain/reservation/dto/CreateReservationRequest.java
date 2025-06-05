package roomescape.domain.reservation.dto;

import java.time.LocalDate;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.PaymentConfirmRequest;

public record CreateReservationRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        PaymentType paymentType,
        PaymentConfirmRequest paymentRequest
) {
}
