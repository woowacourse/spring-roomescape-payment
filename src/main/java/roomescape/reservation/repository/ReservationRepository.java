package roomescape.reservation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationWithPayment;
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
            select new roomescape.reservation.domain.ReservationWithPayment(r, p)
            from Reservation r
            left join Payment p
            on r.id = p.reservation.id
            where r.member.id = :memberId
            """)
    List<ReservationWithPayment> findReservationWithPaymentsByMemberId(Long memberId);

    List<Reservation> findAllByThemeIdAndMemberIdAndDateBetween(
            Long themeId,
            Long memberId,
            LocalDate dateFrom,
            LocalDate dateTo
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
}
