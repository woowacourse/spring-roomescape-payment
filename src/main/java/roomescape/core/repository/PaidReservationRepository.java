package roomescape.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.PaidReservation;

public interface PaidReservationRepository extends JpaRepository<PaidReservation, Long> {
}
