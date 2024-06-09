package roomescape.waiting.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Schedule;
import roomescape.waiting.domain.Waiting;

@Repository
public interface WaitingRepository extends ListCrudRepository<Waiting, Long> {
    List<Waiting> findByMemberId(Long memberId);

    Optional<Waiting> findTopByScheduleIdOrderByCreatedAtAsc(Long scheduleId);

    Long countByScheduleAndCreatedAtLessThanEqual(Schedule schedule, LocalDateTime dateTime);

    boolean existsByScheduleAndMember(Schedule schedule, Member member);
}
