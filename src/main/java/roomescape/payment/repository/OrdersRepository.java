package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.payment.domain.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByPaymentKey(String paymentKey);

}
