package roomescape.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    List<PaymentHistory> findAllByMember_Id(Long memberId);

    void deleteAllByReservation(Reservation reservation);
}
