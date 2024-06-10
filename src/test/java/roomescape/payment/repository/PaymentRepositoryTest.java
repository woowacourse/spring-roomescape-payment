package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Schedule;
import roomescape.reservation.repository.ScheduleRepository;
import roomescape.test.RepositoryTest;

class PaymentRepositoryTest extends RepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("스케줄과 멤버와 상태로 결제를 조회할 수 있다.")
    @Test
    void findByScheduleAndMemberAndStatusTest() {
        Schedule schedule = scheduleRepository.findById(5L).get();
        Member member = memberRepository.findById(2L).get();
        PaymentStatus status = PaymentStatus.PAID;

        Optional<Payment> actual = paymentRepository.findByScheduleAndMemberAndStatus(schedule, member, status);

        assertThat(actual).isNotEmpty();
    }

    @DisplayName("스케줄, 멤버, 상태가 일치하지 않으면 empty를 반환한다.")
    @Test
    void findByScheduleAndMemberAndStatusTest_whenNotExistPayment() {
        Schedule schedule = scheduleRepository.findById(5L).get();
        Member member = memberRepository.findById(2L).get();
        PaymentStatus status = PaymentStatus.REFUND;

        Optional<Payment> actual = paymentRepository.findByScheduleAndMemberAndStatus(schedule, member, status);

        assertThat(actual).isEmpty();
    }
}
