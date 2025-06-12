package roomescape.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.ReservationWithPayment;

public interface JpaReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {
    Optional<Reservation> findByDateAndReservationTimeAndTheme(LocalDate date, ReservationTime time, Theme theme);

    List<Reservation> findByThemeId(Long id);

    List<Reservation> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    List<Reservation> findByDateAndTheme(LocalDate date, Theme theme);

    List<Reservation> findByReservationTimeId(Long reservationTimeId);

    List<Reservation> findByMemberId(Long memberId);

    @Query("""
                    SELECT new roomescape.dto.ReservationWithPayment(r, p)
                    FROM Reservation r
                    LEFT JOIN Payment p ON p.reservation = r
                    WHERE r.member.id = :memberId
            """)
    List<ReservationWithPayment> findAllWithPaymentByMemberId(@Param("memberId") Long memberId);
}
