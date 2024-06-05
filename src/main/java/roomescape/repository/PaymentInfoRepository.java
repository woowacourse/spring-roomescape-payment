package roomescape.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.reservation.PaymentInfo;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {

}
