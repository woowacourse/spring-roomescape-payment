package roomescape.reservation.domain;

import java.util.Comparator;
import java.util.List;

public class Waitings {
    private final List<Reservation> reservations;

    public Waitings(List<Reservation> waitingReservations) {
        reservations = getWaitings(waitingReservations);
    }

    private List<Reservation> getWaitings(List<Reservation> waitingReservations) {
        if (!waitingReservations.isEmpty()) {
            return waitingReservations.stream()
                    .sorted(Comparator.comparing(Reservation::getCreatedAt))
                    .toList();
        }
        return waitingReservations;
    }

    public int findMemberRank(Reservation reservation) {
        if (reservation.isSuccess()) {
            return 0;
        }

        return (int) reservations.stream()
                .filter(waiting -> waiting.getTheme().sameThemeId(reservation.getTheme().getId()))
                .filter(waiting -> waiting.getDate().equals(reservation.getDate()))
                .filter(waiting -> waiting.getTime().getStartAt().equals(reservation.getTime().getStartAt()))
                .takeWhile(waiting -> !waiting.getMember().sameMemberId(reservation.getMember().getId()))
                .count() + 1;
    }

    public Reservation getFirstWaiting() {
        return reservations.get(0);
    }

    public boolean haveWaiting() {
        return !reservations.isEmpty();
    }
}
