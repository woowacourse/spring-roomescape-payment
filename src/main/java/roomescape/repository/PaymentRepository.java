package roomescape.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.NotPayed;
import roomescape.domain.Payment;
import roomescape.domain.Payment.State;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    default Payment getNotPayed() {
        return findByState(State.READY)
                .orElseGet(() -> save(new NotPayed()));
    }

    Optional<Payment> findByState(Payment.State state);
}
