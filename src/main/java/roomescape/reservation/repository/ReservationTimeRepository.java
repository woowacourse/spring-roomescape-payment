package roomescape.reservation.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.common.exception.EntityNotExistException;
import roomescape.reservation.domain.ReservationTime;

public interface ReservationTimeRepository extends JpaRepository<ReservationTime, Long> {

    @Query("select t from ReservationTime t order by t.startAt")
    List<ReservationTime> findAllOrderByStartAt();

    Optional<ReservationTime> findByStartAt(LocalTime startAt);

    @Query("select t from ReservationTime t join Reservation r on t.id = r.time.id where t.id = :id")
    List<ReservationTime> findReservationTimesThatReservationReferById(Long id);

    default ReservationTime fetchById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotExistException("해당 예약의 결제 정보가 없습니다."));
    }
}
