package roomescape.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.member.model.Member;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationWaiting;
import roomescape.domain.reservation.model.Theme;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {

    List<ReservationWaiting> findAllByMemberId(Long memberId);

    Optional<ReservationWaiting> findTopByDateAndTimeAndThemeOrderByCreatedAtAsc(final ReservationDate date, final ReservationTime time, final Theme theme);

    int countAllByThemeAndDateAndTimeAndCreatedAtBefore(Theme theme, ReservationDate date, ReservationTime time, LocalDateTime createdAt);

    boolean existsByMemberAndDateAndTimeAndTheme(Member member, ReservationDate date, ReservationTime time, Theme theme);
}
