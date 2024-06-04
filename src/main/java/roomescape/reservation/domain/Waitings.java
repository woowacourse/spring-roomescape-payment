package roomescape.reservation.domain;

import java.util.List;
import roomescape.reservation.dto.ReservationWithPaymentResponse;

public class Waitings {

    private final List<Reservation> waitings;

    public Waitings(List<Reservation> waitings) {
        this.waitings = List.copyOf(waitings);
    }

    public int findMemberRank(ReservationWithPaymentResponse reservation, Long memberId) {
        if (reservation.getStatus().isSuccess()) {
            return 0;
        }

        return (int) waitings.stream()
                .filter(waiting -> waiting.sameDate(reservation.getDate()))
                .filter(waiting -> waiting.sameThemeId(reservation.getTheme().getId()))
                .filter(waiting -> waiting.sameTimeId(reservation.getTime().getId()))
                .takeWhile(waiting -> !waiting.getMember().sameMemberId(memberId))
                .count() + 1;
    }
}
