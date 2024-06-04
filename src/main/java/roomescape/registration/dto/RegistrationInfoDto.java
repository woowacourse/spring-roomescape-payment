package roomescape.registration.dto;

import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.domain.ReservationStatus;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.domain.WaitingWithRank;

import java.time.LocalDate;
import java.time.LocalTime;

public record RegistrationInfoDto(long id, String themeName, LocalDate date, LocalTime time, String status) {

    public static RegistrationInfoDto from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.waiting();
        Reservation reservation = waiting.getReservation();
        long rank = waitingWithRank.rank();

        return new RegistrationInfoDto(waiting.getId(), reservation.getTheme().getName(),
                reservation.getDate(), reservation.getReservationTime().getStartAt(),
                rank + ReservationStatus.WAITING.getStatus()
        );
    }

    public static RegistrationInfoDto from(ReservationResponse reservationResponse) {
        return new RegistrationInfoDto(reservationResponse.id(), reservationResponse.themeName(),
                reservationResponse.date(), reservationResponse.startAt(),
                ReservationStatus.RESERVED.getStatus()
        );
    }
}
