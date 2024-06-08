package roomescape.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.request.PaymentRequest;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.PaymentFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest extends BaseServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(MemberFixture.prin());
        Theme theme = themeRepository.save(ThemeFixture.theme());
        ReservationTime time = reservationTimeRepository.save(ReservationTimeFixture.ten());
        reservation = reservationRepository.save(ReservationFixture.create(member, time, theme));
    }

    @Test
    @DisplayName("결제를 생성한다.")
    void addPayment() {
        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", BigDecimal.TEN);

        Payment payment = paymentService.addPayment(paymentRequest, reservation);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(payment.getId()).isEqualTo(1L);
            softly.assertThat(payment.getPaymentKey()).isEqualTo(paymentRequest.paymentKey());
            softly.assertThat(payment.getAmount()).isEqualTo(paymentRequest.amount());
        });
    }

    @Test
    @DisplayName("결제를 삭제한다.")
    void deletePaymentById() {
        Payment payment = paymentRepository.save(PaymentFixture.create(reservation));
        long id = payment.getId();

        paymentService.deletePaymentById(id);

        assertThat(paymentRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않은 결제일 경우 예외가 발생한다.")
    void deletePaymentByIdFailWhenNotExist() {
        assertThatThrownBy(() -> paymentService.deletePaymentById(1L))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("결제 정보가 존재하지 않습니다.");
    }
}
