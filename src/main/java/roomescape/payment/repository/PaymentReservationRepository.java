package roomescape.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.member.domain.Member;
import roomescape.payment.domain.PaymentReservation;

public interface PaymentReservationRepository extends JpaRepository<PaymentReservation, Long> {
    List<PaymentReservation> findByReservationMember(final Member member);

    Optional<PaymentReservation> findByPaymentId(final Long paymentId);
}
