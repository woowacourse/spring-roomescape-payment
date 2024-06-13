package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("""
            select count(*)
            from Waiting w
            where w.theme = :theme
            and w.date = :date
            and w.reservationTime = :reservationTime
            and w.id <= :id
            """)
    Long countRankBySameWaiting(Theme theme, LocalDate date, ReservationTime reservationTime, Long id);

    List<Waiting> findAllByMemberId(Long memberId);

    List<Waiting> findAllByStatus(Status status);

    Optional<Waiting> findFirstByDateAndReservationTimeAndThemeAndMember(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            Member member
    );

    Optional<Waiting> findFirstByDateAndReservationTimeAndThemeAndStatus(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            Status status
    );
}
