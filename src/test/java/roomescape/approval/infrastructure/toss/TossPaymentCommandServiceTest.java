package roomescape.approval.infrastructure.toss;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import roomescape.approval.domain.ApprovalType;
import roomescape.approval.domain.Payment;
import roomescape.approval.infrastructure.ApprovalRepositoryAdapter;
import roomescape.approval.infrastructure.toss.client.FakeTossPaymentClient;
import roomescape.fixture.MemberFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.infrastructure.MemberRepositoryAdapter;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationSpec;
import roomescape.reservation.domain.ReservationState;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.infrastructure.ReservationRepositoryAdapter;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.respository.ReservationTimeRepository;
import roomescape.reservationTime.infrastructure.ReservationTimeRepositoryAdapter;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;
import roomescape.theme.infrastructure.ThemeRepositoryAdapter;

@ActiveProfiles("test")
@DataJpaTest
@Import({
        TossPaymentCommandService.class,
        ApprovalRepositoryAdapter.class,
        ReservationRepositoryAdapter.class,
        MemberRepositoryAdapter.class,
        ThemeRepositoryAdapter.class,
        ReservationTimeRepositoryAdapter.class,
        FakeTossPaymentClient.class
})
class TossPaymentCommandServiceTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private TossPaymentCommandService tossPaymentCommandService;

    @DisplayName("Payment 클래스를 지원하는지 확인한다")
    @Test
    void supports() {
        // when
        boolean result = tossPaymentCommandService.supports(ApprovalType.PAYMENT);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("Payment 클래스가 아닌 경우 지원하지 않는다")
    @Test
    void notSupports() {
        // when
        boolean result = tossPaymentCommandService.supports(ApprovalType.ONSITE);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("Payment을 승인한다")
    @Test
    void approve() {
        // given
        Member member = MemberFixture.createMember("test", "test@example.com", "password");
        memberRepository.save(member);
        Theme theme = new Theme("테마", "설명", "썸네일", BigDecimal.valueOf(10000));
        themeRepository.save(theme);
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        timeRepository.save(time);
        ReservationSpec spec = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time, theme);
        Reservation reservation = new Reservation(member, spec);
        reservationRepository.save(reservation);
        Payment payment = new Payment(reservation, "testId", "testKey", BigDecimal.valueOf(10000));

        // when
        tossPaymentCommandService.approve(payment);

        // then
        assertThat(payment.isApproved()).isTrue();
        assertThat(reservation.getReservationState()).isEqualTo(ReservationState.APPROVED);
    }
}
