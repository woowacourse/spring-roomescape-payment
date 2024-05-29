package roomescape.reservation.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.member.model.Member;
import roomescape.reservation.model.ReservationDate;
import roomescape.reservation.model.ReservationTime;
import roomescape.reservation.model.ReservationWaiting;
import roomescape.reservation.model.Theme;

public interface ReservationWaitingRepository extends JpaRepository<ReservationWaiting, Long> {

    Optional<ReservationWaiting> findTopByDateAndTimeAndThemeOrderByCreatedAtAsc(final ReservationDate date,
                                                                                 final ReservationTime time,
                                                                                 final Theme theme);

    boolean existsByMemberAndDateAndTimeAndTheme(Member member,
                                                 ReservationDate date,
                                                 ReservationTime time,
                                                 Theme theme);
}
