package roomescape.payment.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import roomescape.payment.entity.Payment;

public interface JpaPaymentRepository extends CrudRepository<Payment, Long> {

    @Query("""
            SELECT p FROM Payment p
            JOIN FETCH p.reservation r
            JOIN FETCH r.member
            JOIN FETCH r.theme
            JOIN FETCH r.time
            """)
    List<Payment> findAll();

    void deleteByReservationId(final Long reservationId);
}
