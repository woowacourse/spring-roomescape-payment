package roomescape.domain.reservation.repository;

import roomescape.domain.reservation.dto.SearchReservationsParams;
import roomescape.domain.reservation.model.Reservation;

import java.util.List;

public interface CustomReservationRepository {

    List<Reservation> searchReservations(SearchReservationsParams searchReservationsParams);
}
