package roomescape.reservation.service.dto;

import roomescape.reservation.controller.dto.WaitingResponse;

public record MyReservationInfo(long id,
                                ReservationInfo reservationInfo,
                                WaitingResponse waitingResponse,
                                PaymentInfo paymentInfo) {
}
