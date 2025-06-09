package roomescape.reservationtime;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.Optional;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {
    Boolean existsByStartAt(LocalTime startAt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select t from ReservationTime t where t.id = :id")
    Optional<ReservationTime> findByIdForUpdate(Long id);
}
