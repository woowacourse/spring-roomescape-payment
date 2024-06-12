package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;

@Tag(name = "예약 응답", description = "사용자에게 보여줄 예약 정보를 응답한다.")
public record ReservationResponse(
        long id,
        String memberName,
        String themeName,
        LocalDate date,
        LocalTime startAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt());
    }

    public static ReservationResponse from(ReservationDto reservationdto) {
        return new ReservationResponse(reservationdto.id(), reservationdto.memberName(),
                reservationdto.themeName(), reservationdto.date(),
                reservationdto.startAt());
    }

}
