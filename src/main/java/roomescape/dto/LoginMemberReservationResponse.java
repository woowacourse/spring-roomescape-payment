package roomescape.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LoginMemberReservationResponse(long reservationId,
                                             String theme,
                                             LocalDate date,
                                             LocalTime time,
                                             ReservationStatus status,
                                             String paymentKey,
                                             Long amount) {
}
