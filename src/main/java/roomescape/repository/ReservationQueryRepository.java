package roomescape.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationWithPayment;

public interface ReservationQueryRepository extends JpaRepository<Reservation, Long> {
    @Query("""
            SELECT new roomescape.domain.reservation.ReservationWithPayment(
            r,
            p.paymentKey,
            p.amount
            )
            FROM Reservation r
            JOIN Payment p
            ON p.reservation.id = r.id
            WHERE r.member.id = :memberId
            """
    )
    List<ReservationWithPayment> findReservationsWithPaymentByMemberId(@Param("memberId") Long memberId);
}
