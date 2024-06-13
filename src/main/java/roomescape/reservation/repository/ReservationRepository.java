package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationWithRank;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r.reservationTime.id
            from Reservation r
            where r.date = :date
            and r.theme.id = :themeId
            """)
    List<Long> findTimeIdsByDateAndThemeId(LocalDate date, Long themeId);

    @Query("""
                SELECT new roomescape.reservation.domain.ReservationWithRank(
                    r, (SELECT COUNT(*) FROM Reservation r2
                        WHERE r2.theme = r.theme
                        AND r2.date = r.date
                        AND r2.reservationTime = r.reservationTime
                        AND r2.id < r.id))
                    FROM Reservation r
                    WHERE r.member.id = :memberId
            """)
    List<ReservationWithRank> findReservationWithRanksByMemberId(Long memberId);

    List<Reservation> findAllByMemberId(Long memberId);

    List<Reservation> findAllByThemeIdAndMemberIdAndDateBetweenAndStatus(
            Long themeId,
            Long memberId,
            LocalDate dateFrom,
            LocalDate dateTo,
            Status status
    );

    List<Reservation> findAllByStatus(Status status);

    List<Reservation> findAllByDateAndReservationTimeAndTheme(
            LocalDate date,
            ReservationTime time,
            Theme theme
    );

    Optional<Reservation> findFirstByDateAndReservationTimeAndTheme(
            LocalDate date,
            ReservationTime time,
            Theme theme
    );

    Optional<Reservation> findFirstByDateAndReservationTimeAndThemeAndMember(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            Member member
    );

    Optional<Reservation> findFirstByDateAndReservationTimeAndThemeAndStatus(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            Status status
    );
}
