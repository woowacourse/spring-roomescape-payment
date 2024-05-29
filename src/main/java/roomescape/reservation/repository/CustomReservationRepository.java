package roomescape.reservation.repository;

import java.util.List;
import roomescape.reservation.dto.SearchReservationsParams;
import roomescape.reservation.model.Reservation;

public interface CustomReservationRepository {

    List<Reservation> searchReservations(SearchReservationsParams searchReservationsParams);
}
