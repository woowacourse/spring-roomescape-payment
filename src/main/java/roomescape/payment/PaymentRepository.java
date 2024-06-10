package roomescape.payment;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

@Tag(name = "결제 레포지토리", description = "결제 DB에 저장된 데이터를 제어하여 특정 값을 반환한다.")
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findPaymentByReservationId(long reservationId);
}
