package roomescape.domain.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.payment.ReservationPayment;

public interface ReservationPaymentRepository extends JpaRepository<ReservationPayment, Long> {

    List<ReservationPayment> findAllByReservation_Member_Id(Long memberId);
}
