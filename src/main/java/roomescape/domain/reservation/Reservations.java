package roomescape.domain.reservation;

import java.util.List;

public class Reservations {

    private final List<Reservation> values;

    public Reservations(final List<Reservation> values) {
        this.values = values;
    }

    public List<Long> getIds() {
        return values.stream()
                .map(Reservation::getId)
                .toList();
    }

    public List<Reservation> filterStatus(final Status status) {
        return values.stream()
                .filter(reservation -> reservation.getStatus() == status)
                .toList();
    }
}
