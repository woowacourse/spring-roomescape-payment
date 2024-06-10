package roomescape.registration.domain.reservation.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.registration.domain.reservation.domain.Reservation;

@Tag(name = "예약 dto", description = "예약 정보 중 중요한 정보를 전달한다.")
public record ReservationDto(
        long id,
        String memberName,
        String themeName,
        LocalDate date,
        LocalTime startAt
) {
    public static ReservationDto from(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getMember().getName(),
                reservation.getTheme().getName(), reservation.getDate(),
                reservation.getReservationTime().getStartAt()
        );
    }
}
