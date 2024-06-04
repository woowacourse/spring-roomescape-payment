package roomescape.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationPayment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    boolean existsByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    boolean existsByTimeId(Long id);

    List<Reservation> findAllByDateAndThemeId(ReservationDate date, Long themeId);

    boolean existsByThemeId(Long id);

    Optional<Reservation> findByDateAndTimeIdAndThemeId(ReservationDate date, Long timeId, Long themeId);

    @Query("""
            SELECT new roomescape.domain.ReservationPayment(
                r,
                p.id,
                p.paymentKey,
                p.orderId,
                p.totalAmount)
            FROM Reservation r
            LEFT JOIN Payment p
            ON r.id = p.reservation.id
            WHERE r.member.id = :memberId
            """)
    List<ReservationPayment> findReservationPaymentByMemberId(Long memberId);
}
