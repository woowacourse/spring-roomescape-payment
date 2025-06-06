package roomescape.reservationslot.presentation.dto.response;

import roomescape.reservation.domain.ReservationStatus;

public record MyReservationResponse(Long reservationSlotId,
                                    String theme,
                                    String date,
                                    String time,
                                    ReservationStatus status,
                                    String paymentKey,
                                    Long amount,
                                    long waitingRank) {

}
