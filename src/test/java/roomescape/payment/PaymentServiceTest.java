package roomescape.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import roomescape.fixture.MemberFixture;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.infrastructure.MemberRepositoryAdapter;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.Amount;
import roomescape.payment.domain.OrderId;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentKey;
import roomescape.payment.infrastructure.PaymentRepositoryAdapter;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationSpec;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.infrastructure.ReservationRepositoryAdapter;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.respository.ReservationTimeRepository;
import roomescape.reservationTime.infrastructure.ReservationTimeRepositoryAdapter;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;
import roomescape.theme.infrastructure.ThemeRepositoryAdapter;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@Import({
        PaymentService.class,
        PaymentRepositoryAdapter.class,
        ReservationRepositoryAdapter.class,
        ThemeRepositoryAdapter.class,
        ReservationTimeRepositoryAdapter.class,
        MemberRepositoryAdapter.class
})
public class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository timeRepository;

    @DisplayName("결제 저장 - 성공")
    @Test
    void create_success() {
        // given
        // 결제 paymentKey 설정
        String key = "qwer1234";
        // 주문 orderId 설정
        String orderId = "orderId1234";
        // 가격 설정
        Amount amount = new Amount(BigDecimal.valueOf(1000L));
        // given
        // 회원 생성 및 저장
        Member member = MemberFixture.createMember("에드", "ed@example.com", "password123");
        memberRepository.save(member);
        Long memberId = member.getId();

        // 오전 10시 예약 시간 생성 및 저장
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        timeRepository.save(time);

        // 테마 생성 및 저장
        Theme theme = new Theme("테마", "설명", "썸네일");
        themeRepository.save(theme);

        // 내일 날짜로 예약 날짜 설정
        LocalDate date = LocalDate.now().plusDays(1);
        // 예약 스펙 생성 (날짜, 시간, 테마)
        ReservationSpec spec = new ReservationSpec(new ReservationDate(date), time, theme);

        // 회원으로 예약 생성 및 저장
        Reservation reservation = new Reservation(member, spec);
        Reservation paymentReservation = reservationRepository.save(reservation);
        // 결제 객체 생성
        Payment payment = new Payment(new PaymentKey(key), new OrderId(orderId), amount, paymentReservation.getId());

        // when
        Payment resultPayment = paymentService.save(payment);

        // then
        assertThat(resultPayment).isNotNull();
        assertThat(resultPayment.getId()).isNotNull();
        assertThat(resultPayment.getPaymentKey().getValue()).isEqualTo(key);
        assertThat(resultPayment.getAmount().getValue()).isEqualByComparingTo(amount.getValue());
        assertThat(resultPayment.getReservationId()).isEqualTo(paymentReservation.getId());
    }

    @DisplayName("회원 ID로 결제 목록 조회 - 성공")
    @Test
    void findAllByMemberId_success() {
        // given
        Member member = MemberFixture.createMember("루카", "luca@example.com", "secure123");
        memberRepository.save(member);
        Long memberId = member.getId();

        ReservationTime time1 = new ReservationTime(LocalTime.of(10, 0));
        ReservationTime time2 = new ReservationTime(LocalTime.of(14, 0));
        timeRepository.save(time1);
        timeRepository.save(time2);

        Theme theme = new Theme("미스터리룸", "공포 테마", "thumbnail.jpg");
        themeRepository.save(theme);

        ReservationSpec spec1 = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time1, theme);
        ReservationSpec spec2 = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(2)), time2, theme);

        Reservation reservation1 = reservationRepository.save(new Reservation(member, spec1));
        Reservation reservation2 = reservationRepository.save(new Reservation(member, spec2));

        Payment payment1 = new Payment(
                new PaymentKey("payKey-1"),
                new OrderId("orderId-1"),
                new Amount(BigDecimal.valueOf(1500)),
                reservation1.getId()
        );
        Payment payment2 = new Payment(
                new PaymentKey("payKey-2"),
                new OrderId("orderId-2"),
                new Amount(BigDecimal.valueOf(2000)),
                reservation2.getId()
        );

        paymentService.save(payment1);
        paymentService.save(payment2);

        // when
        List<Payment> result = paymentService.findAllByMemberId(memberId);

        // then
        assertThat(result)
                .extracting(Payment::getPaymentKey)
                .extracting(PaymentKey::getValue)
                .containsExactlyInAnyOrder("payKey-1", "payKey-2");
    }
}
