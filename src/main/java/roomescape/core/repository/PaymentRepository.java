package roomescape.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.core.domain.Member;
import roomescape.core.domain.Payment;
import roomescape.core.domain.PaymentStatus;
import roomescape.core.domain.Reservation;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation(final Reservation reservation);

    boolean existsByReservation(final Reservation reservation);

    List<Payment> findAllByMemberAndStatus(final Member member, final PaymentStatus status);
}
