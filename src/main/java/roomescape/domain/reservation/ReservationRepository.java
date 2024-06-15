package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.domain.dto.AvailableTimeDto;
import roomescape.domain.theme.Theme;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    long countByTimeIdAndStatusIn(long timeId, List<ReservationStatus> status);

    boolean existsByDateAndTimeIdAndThemeIdAndStatusIn(
            LocalDate date, long timeId, long themeId, List<ReservationStatus> status);

    boolean existsByDateAndTimeIdAndThemeIdAndMemberIdAndStatusIn(
            LocalDate date, long timeId, long themeId, long memberId, List<ReservationStatus> status);

    List<Reservation> findAllByMemberIdAndStatusIn(Long memberId, List<ReservationStatus> status);

    @Query("""
            select new roomescape.domain.dto.AvailableTimeDto(rt.id, rt.startAt,
            (select count(r) > 0 
                from Reservation r 
                where r.time.id = rt.id 
                and r.date = :date 
                and r.theme.id = :themeId
                and (r.status = 'RESERVATION' or r.status = 'PAYMENT_WAITING')))
            from ReservationTime rt
            """)
    List<AvailableTimeDto> findAvailableReservationTimes(LocalDate date, long themeId);

    @Query("""
            select t from Theme t
            join Reservation r on r.theme.id = t.id
            and r.date between :startDate and :endDate
            and (r.status = 'RESERVATION' or r.status = 'PAYMENT_WAITING')
            group by t.id, t.name, t.description, t.thumbnail
            order by count(r.id) desc
            """)
    List<Theme> findPopularThemesDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
            select r from Reservation r
            where r.theme.id = :themeId
            and r.member.id = :memberId 
            and :dateFrom <= r.date
            and r.date <= :dateTo 
            and (r.status = 'RESERVATION' or r.status = 'PAYMENT_WAITING')
            """)
    List<Reservation> findByCriteria(Long themeId, Long memberId, LocalDate dateFrom, LocalDate dateTo);

    List<Reservation> findAllByStatusIn(List<ReservationStatus> status);
}
