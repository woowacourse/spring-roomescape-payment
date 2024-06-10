package roomescape.registration.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.waiting.domain.Waiting;

@Tag(name = "등록 dto", description = "예약 혹은 예약 대기 데이터를 전달한다.")
public record RegistrationDto(LocalDate date, long themeId, long timeId, long memberId) {

    public static RegistrationDto from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();

        return new RegistrationDto(reservation.getDate(), reservation.getTheme().getId(),
                reservation.getReservationTime().getId(), reservation.getMember().getId());
    }
}
