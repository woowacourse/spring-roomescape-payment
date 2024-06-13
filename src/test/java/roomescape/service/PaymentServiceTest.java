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
import roomescape.service.dto.request.PaymentCreateRequest;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.PaymentFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

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
        PaymentCreateRequest paymentConfirmRequest = new PaymentCreateRequest("paymentKey", "orderId", BigDecimal.TEN, reservation);

        Payment payment = paymentService.addPayment(paymentConfirmRequest);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(payment.getId()).isEqualTo(1L);
            softly.assertThat(payment.getPaymentKey()).isEqualTo(paymentConfirmRequest.paymentKey());
            softly.assertThat(payment.getAmount()).isEqualTo(paymentConfirmRequest.amount());
        });
    }

    @Test
    @DisplayName("예약으로 결제를 삭제한다.")
    void deletePaymentById() {
        Payment payment = paymentRepository.save(PaymentFixture.create(reservation));
        long id = payment.getId();

        paymentService.deleteByReservation(reservation);

        assertThat(paymentRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("예약으로 결제를 삭제할 때 결제가 존재하지 않을 경우 예외가 발생한다.")
    void deletePaymentByIdFailWhenNotExist() {
        assertThatThrownBy(() -> paymentService.deleteByReservation(reservation))
                .isExactlyInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("결제 정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("결제를 삭제한 후 롤백한다.")
    void rollbackDelete() {
        Payment payment = paymentRepository.save(PaymentFixture.create(reservation));
        paymentService.deleteByReservation(reservation);

        paymentService.rollbackDelete(payment);

        assertThat(paymentRepository.findAll()).hasSize(1);
    }
}
