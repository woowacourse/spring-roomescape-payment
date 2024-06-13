package roomescape.paymenthistory.repository;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import roomescape.paymenthistory.domain.PaymentHistory;

public interface PaymentHistoryRepository extends Repository<PaymentHistory, Long> {

    void save(PaymentHistory paymentHistory);

    Optional<PaymentHistory> findByReservation_Id(Long reservationId);

    void deleteByReservation_Id(Long reservationId);
}
