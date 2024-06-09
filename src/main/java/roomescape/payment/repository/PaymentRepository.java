package roomescape.payment.repository;

import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Schedule;

public interface PaymentRepository extends ListCrudRepository<Payment, Long> {
    Optional<Payment> findByScheduleAndMemberAndStatus(Schedule schedule, Member member, PaymentStatus status);
}
