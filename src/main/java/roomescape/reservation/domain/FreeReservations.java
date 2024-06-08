package roomescape.reservation.domain;

import java.util.ArrayList;
import java.util.List;

public class FreeReservations {
    private final List<Reservation> reservations;

    public FreeReservations(List<Reservation> allList, List<Reservation> paidReservation) {
        allList.removeAll(paidReservation);
        reservations = allList;
    }

    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }
}
