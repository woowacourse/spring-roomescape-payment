package roomescape.repository.dto;

import roomescape.domain.reservation.PaymentInfo;
import roomescape.domain.reservation.Reservation;

public record MyReservationDto(
    Reservation reservation,
    Long rank,
    PaymentInfo paymentInfo
) { }
