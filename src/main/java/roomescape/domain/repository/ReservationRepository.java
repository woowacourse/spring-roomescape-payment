package roomescape.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationSlot;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select r from Reservation r where r.reservationSlot.date = :date and r.reservationSlot.theme = :theme")
    List<Reservation> findByDateAndTheme(@Param("date") LocalDate date, @Param("theme") Theme theme);

    @Query("select r from Reservation r where r.reservationSlot.date between :startDate and :endDate")
    List<Reservation> findAllByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            select r
            from Reservation r
            join fetch r.reservationSlot
            join fetch r.member
            where (:startDate is null or r.reservationSlot.date>= :startDate)
                and (:endDate is null or r.reservationSlot.date <= :endDate)
                and (:themeId is null or r.reservationSlot.theme.id = :themeId)
                and (:memberId is null or r.member.id = :memberId)""")
    List<Reservation> findByConditions(
            @Param("startDate") Optional<LocalDate> startDate,
            @Param("endDate") Optional<LocalDate> endDate,
            @Param("themeId") Long themeId,
            @Param("memberId") Long memberId
    );

    @Query("""
            select r
            from Reservation r
            where r.reservationSlot.date = :date
             and r.reservationSlot.theme.id =:themeId
             and r.reservationSlot.time.id = :timeId
             """)
    Optional<Reservation> findByDateAndThemeIdAndTimeId(@Param("date") LocalDate date, @Param("themeId") long themeId, @Param("timeId") long timeId);

    @Query("""
        select r
        from Reservation r
        join fetch r.reservationSlot.time
        join fetch r.reservationSlot.theme
        where r.member = :member
        and r.reservationSlot.date >= :date
        order by r.reservationSlot.date asc, r.reservationSlot.time.startAt asc
    """)
    List<Reservation> findByMemberAndDateGreaterThanEqual(@Param("member") Member member, @Param("date") LocalDate date);

    boolean existsByReservationSlot(ReservationSlot slot);

    boolean existsByMemberAndReservationSlot(Member member, ReservationSlot reservationSlot);

    @Query("select exists(select 1 from Reservation r where r.reservationSlot.time=:time)")
    boolean existsByTime(@Param("time") ReservationTime time);

    @Query("select exists(select 1 from Reservation r where r.reservationSlot.theme=:theme)")
    boolean existsByTheme(@Param("theme") Theme theme);
}
