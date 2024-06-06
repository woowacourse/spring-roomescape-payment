package roomescape.repository;

import org.springframework.data.repository.CrudRepository;
import roomescape.model.PaymentInfo;
import roomescape.model.Reservation;

import java.util.Optional;

public interface PaymentInfoRepository extends CrudRepository<PaymentInfo, Long> {

    Optional<PaymentInfo> findByReservation(Reservation reservation);

    PaymentInfo save(PaymentInfo paymentInfo);

    Optional<PaymentInfo> findById(Long id);
}
