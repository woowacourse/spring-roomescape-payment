package roomescape.domain.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.dto.response.reservation.CanceledReservationResponse;

public interface CanceledReservationRepository extends JpaRepository<CanceledReservation, Long> {
    @Query("""
            select new roomescape.dto.response.reservation.CanceledReservationResponse(
            cr, cp.paymentKey, cp.totalAmount
            )
            from CanceledReservation cr
            join fetch ReservationTime rt on rt.id = cr.time.id
            join fetch Theme t on t.id = cr.theme.id
            join fetch Member m on m.id = cr.member.id
            left join fetch CanceledPayment cp on cp.canceledReservation.id = cr.id
            """)
    List<CanceledReservationResponse> findAllCanceledReservationInform();
}
