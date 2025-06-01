package roomescape.domain.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import roomescape.domain.payment.ReservationPayment;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {

    List<ReservationPayment> findAllByReservation_Member_Id(@Param("memberId") Long memberId);
}
