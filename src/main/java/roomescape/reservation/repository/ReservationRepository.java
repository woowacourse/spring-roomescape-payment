package roomescape.reservation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import roomescape.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    List<Reservation> findAllByMember_Id(final Long memberId);

    boolean existsByScheduleId(Long scheduleId);

    Optional<Reservation> findByScheduleId(Long scheduleId);

    boolean existsBySchedule_ReservationTime_Id(Long reservationTimeId);

    boolean existsBySchedule_Theme_Id(Long themeId);
}
