package roomescape.domain.reservation;

import org.springframework.data.repository.ListCrudRepository;

public interface PaymentRepository extends ListCrudRepository<Payment, Long> {

    void deleteByReservationId(Long reservationId);
}
