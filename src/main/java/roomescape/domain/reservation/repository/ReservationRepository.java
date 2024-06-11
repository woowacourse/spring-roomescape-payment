package roomescape.domain.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByDateAndTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    @Query("""
            select new roomescape.domain.reservation.dto.BookedReservationReadOnly(
                r.bookedMember.id,
                r.id,
                r.bookedMember.member.name,
                r.date,
                r.time.startAt,
                r.theme.name
            )
            from Reservation r
            where (:startDate is null or r.date >= :startDate)
                and (:endDate is null or r.date <= :endDate)
                and (:themeId is null or r.theme.id = :themeId)
                and (:memberId is null or r.bookedMember.member.id = :memberId)""")
    List<BookedReservationReadOnly> findByConditions(@Nullable LocalDate startDate, @Nullable LocalDate endDate, @Nullable Long themeId,
                                                     @Nullable Long memberId);

    @Query("""
            select r.theme
            from Reservation r
            where r.date between :startDate and :endDate
            group by r.theme
            order by count(r) desc
            """)
    List<Theme> findPopularThemes(LocalDate startDate, LocalDate endDate, Limit limit);

    @Query("select r.time from Reservation r where r.date = :date and r.theme = :theme and r.bookedMember is not null")
    List<ReservationTime> findBookedTimesByDateAndTheme(LocalDate date, Theme theme);

    boolean existsByTime(ReservationTime time);

    boolean existsByTheme(Theme theme);
}
