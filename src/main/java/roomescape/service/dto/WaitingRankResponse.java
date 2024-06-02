package roomescape.service.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.WaitingMember;
import roomescape.domain.reservation.dto.WaitingRank;

public record WaitingRankResponse(
        Long id,
        String name,
        String theme,
        LocalDate date,
        LocalTime startAt,
        Long rank
) {
    public static WaitingRankResponse from(WaitingRank waitingRank) {
        WaitingMember waitingMember = waitingRank.waitingMember();
        Reservation reservation = waitingMember.getReservation();

        return new WaitingRankResponse(
                waitingMember.getId(),
                waitingMember.getMember().getName(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                waitingRank.rank()
        );
    }
}
