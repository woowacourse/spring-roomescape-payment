package roomescape.booking.waiting;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import roomescape.schedule.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    List<Waiting> findAllByMember_Email(String email);

    Waiting findFirstByScheduleOrderByCreatedAtAsc(Schedule schedule);

    boolean existsBySchedule(Schedule schedule);

    Long countByScheduleAndCreatedAtLessThan(Schedule schedule, LocalDateTime createdAt);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Waiting w where w.id = :id")
    Optional<Waiting> findByIdForUpdate(Long id);
}
