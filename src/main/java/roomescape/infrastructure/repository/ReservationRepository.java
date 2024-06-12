package roomescape.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWithRank;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.Theme;
import roomescape.exception.reservation.NotFoundReservationException;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    default Reservation getReservationById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundReservationException::new);
    }

    List<Reservation> findAllByStatus(Status status);

    @Query(""" 
            select r from Reservation r
            where r.date >= :start
            and r.date <= :end
            and r.member.id = :memberId
            and r.theme.id = :themeId
            """)
    List<Reservation> findByPeriodAndThemeAndMember(
            @Param("start") LocalDate start, @Param("end") LocalDate end,
            @Param("memberId") Long memberId, @Param("themeId") Long themeId
    );

    @Query(""" 
            select new roomescape.domain.reservation.ReservationWithRank(
                 r.id, r.theme.name, r.date, r.time.startAt, r.status, p.paymentKey, p.amount,
                 (
                     select count(r2) AS waiting_rank
                     from Reservation r2
                     where r.createdAt >= r2.createdAt 
                     and r.time = r2.time 
                     and r.date = r2.date 
                     and r.theme = r2.theme
                 )
            )
            from Reservation r
            left join r.payment p
            where r.member.id = :memberId
            order by r.id desc 
             """)
    List<ReservationWithRank> findWithRank(@Param("memberId") Long memberId);

    @Query("""
            select r from Reservation r
            where r.theme = :theme
            and r.date = :date
            and r.time = :time
            and r.status = 'WAITING'
            order by r.createdAt
            limit 1
            """)
    Optional<Reservation> findNextWaiting(Theme theme, LocalDate date, ReservationTime time);

    boolean existsByThemeAndDateAndTimeAndStatusIn(Theme theme, LocalDate date, ReservationTime time,
                                                   List<Status> status);

    boolean existsByThemeAndDateAndTimeAndMemberAndStatusIn(Theme theme, LocalDate date, ReservationTime time,
                                                            Member member, List<Status> status);

    boolean existsByThemeId(long themeId);

    boolean existsByTimeId(long timeId);
}
