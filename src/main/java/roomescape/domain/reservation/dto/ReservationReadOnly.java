package roomescape.domain.reservation.dto;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.slot.ReservationTime;
import roomescape.domain.reservation.slot.Theme;

import java.time.LocalDate;

public record ReservationReadOnly(
        Long id,
        Member member,
        LocalDate date,
        ReservationTime time,
        Theme theme
) {
}
