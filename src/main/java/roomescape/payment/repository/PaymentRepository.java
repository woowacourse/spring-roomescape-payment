package roomescape.payment.repository;

import org.springframework.data.repository.Repository;
import roomescape.payment.domain.Payment;

public interface PaymentRepository extends Repository<Payment, Long> {

    void save(Payment payment);

    Payment findByReservation_Id(Long reservationId);

    void deleteByReservation_Id(Long reservationId);
}
