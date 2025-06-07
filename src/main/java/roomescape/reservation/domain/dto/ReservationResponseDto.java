package roomescape.reservation.domain.dto;

import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.dto.ThemeResponseDto;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.domain.dto.UserResponseDto;

import java.time.LocalDate;

public record ReservationResponseDto(
        Long id,
        LocalDate date,
        ReservationTimeResponseDto time,
        ThemeResponseDto theme,
        UserResponseDto user
) {
    public static ReservationResponseDto of(Reservation reservation) {
        ReservationTimeResponseDto reservationTimeResponseDto = ReservationTimeResponseDto.of(
                reservation.getReservationTime());
        ThemeResponseDto themeResponseDto = ThemeResponseDto.of(reservation.getTheme());
        UserResponseDto userResponseDto = UserResponseDto.of(reservation.getUser());

        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getDate(),
                reservationTimeResponseDto,
                themeResponseDto,
                userResponseDto
        );
    }

    public Reservation toReservation() {
        return new Reservation(
                id,
                date,
                ReservationStatus.BOOKED,
                new ReservationTime(time.id(), time.startAt()),
                new Theme(theme.id(), theme.name(), theme.description(), theme.thumbnail()),
                new User(user.id(), Role.valueOf(user.roleName()), user.name(), user.email(), user.password())
        );
    }
}

