package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.PaymentResult;

@Repository
public interface PaymentResultRepository extends JpaRepository<PaymentResult, Long> {

}
