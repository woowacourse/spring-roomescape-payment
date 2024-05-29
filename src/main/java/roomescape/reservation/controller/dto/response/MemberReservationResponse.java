package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithRank;

public record MemberReservationResponse(
        long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        long waitingRank
) {

    public static MemberReservationResponse toResponse(ReservationWithRank reservationWithRank) {
        Reservation reservation = reservationWithRank.getWaiting();

        return new MemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                reservation.getStatusDisplayName(),
                reservationWithRank.getRank()
        );
    }
}
