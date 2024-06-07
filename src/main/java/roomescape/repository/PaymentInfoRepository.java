package roomescape.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.model.PaymentInfo;

import java.util.Optional;

public interface PaymentInfoRepository extends CrudRepository<PaymentInfo, Long> {

    @Query("""
            SELECT p
            FROM PaymentInfo p
            WHERE p.reservation.id = :reservationId
            """)
    Optional<PaymentInfo> findByReservationId(Long reservationId);

    PaymentInfo save(PaymentInfo paymentInfo);

    Optional<PaymentInfo> findById(Long id);
}
