package roomescape.registration.dto;

import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.waiting.domain.Waiting;

import java.time.LocalDate;

public record RegistrationDto(
        LocalDate date,
        long themeId,
        long timeId,
        long memberId) {

    public static RegistrationDto from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();

        return new RegistrationDto(reservation.getDate(), reservation.getTheme().getId(),
                reservation.getReservationTime().getId(), reservation.getMember().getId());
    }

    public static RegistrationDto of(ReservationRequest reservationRequest, long id) {
        return new RegistrationDto(
                reservationRequest.date(),
                reservationRequest.themeId(),
                reservationRequest.timeId(),
                id);
    }
}
