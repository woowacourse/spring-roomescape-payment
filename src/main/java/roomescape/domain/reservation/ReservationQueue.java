package roomescape.domain.reservation;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ReservationQueue {

    public static final int ORDERING_START_INDEX = 1;

    private final List<Reservation> queue;

    public ReservationQueue(final Collection<Reservation> reservations) {
        this.queue = sortByCreatedAt(reservations);
    }

    private List<Reservation> sortByCreatedAt(final Collection<Reservation> reservations) {
        return reservations.stream()
            .sorted(Comparator.comparing(Reservation::createdAt))
            .collect(toList());
    }

    public int orderOf(final Reservation reservation) {
        if (queue.isEmpty() || !queue.contains(reservation)) {
            throw new IllegalArgumentException("해당 예약은 대기열에 존재하지 않습니다 : " + reservation);
        }
        return ORDERING_START_INDEX + queue.indexOf(reservation);
    }
    
    public Optional<Reservation> findNext(final Reservation reservation) {
        var orderOfNext = orderOf(reservation) + 1;
        if (queue.size() < orderOfNext) {
            return Optional.empty();
        }

        var indexOfNext = orderOfNext - ORDERING_START_INDEX;
        var next = queue.get(indexOfNext);
        return Optional.of(next);
    }

    public void remove(final Reservation reservation) {
        if (orderOf(reservation) == ORDERING_START_INDEX) {
            findNext(reservation).ifPresent(Reservation::pend);
        }
        queue.remove(reservation);
    }
}
