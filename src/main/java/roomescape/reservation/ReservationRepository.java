package roomescape.reservation;

import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateAndThemeId(String date, Long themeId);

    List<Reservation> findByMemberId(Long memberId);

    List<Reservation> findByThemeIdAndDateAndTimeId(Long themeId, String date, Long timeId);

    @Query("""
        SELECT new roomescape.reservation.ReservationPaymentInfo(r, p.paymentKey, p.totalAmount) 
        FROM Reservation r LEFT JOIN Payment p ON r.id = p.reservationId WHERE r.member.id = :memberId
        """)
    List<ReservationPaymentInfo> findPaymentByMemberId(Long memberId);
}
