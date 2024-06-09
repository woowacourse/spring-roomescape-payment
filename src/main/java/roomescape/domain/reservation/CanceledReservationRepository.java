package roomescape.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CanceledReservationRepository extends JpaRepository<CanceledReservation, Long> {
}
