package roomescape.reservation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.dto.MyReservationWithTossPayment;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {

    boolean existsByScheduleId(Long scheduleId);

    Optional<Reservation> findByScheduleId(Long scheduleId);

    boolean existsBySchedule_ReservationTime_Id(Long reservationTimeId);

    boolean existsBySchedule_Theme_Id(Long themeId);

    @Query("""
    SELECT new roomescape.reservation.repository.dto.MyReservationWithTossPayment(
        r.id,
        s.theme.name.name,
        s.reservationDate.date,
        s.reservationTime.startAt,
        p.paymentKey.paymentKey,
        p.amount.amount
    )
    FROM Reservation r
    JOIN r.schedule s
    LEFT JOIN Payment p ON p.reservation = r
    WHERE r.member.id = :memberId
""")
    List<MyReservationWithTossPayment> findAllWithPaymentByMemberId(@Param("memberId") Long memberId);
}
