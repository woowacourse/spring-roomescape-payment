package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.PaymentNotFoundException;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class PaymentServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    private PaymentService paymentService;
    private Reservation reservation;
    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository);

        time = reservationTimeRepository.save(ReservationTime.withUnassignedId(LocalTime.of(10, 0)));
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        member = memberRepository.save(TestFixture.makeMember());
        reservation = reservationRepository.save(
                TestFixture.makeReservation(TestFixture.makeFutureDate(), time, member, theme)
        );
    }

    @Test
    void createPaymentWithRequest_shouldCreatePayment() {
        PaymentRequest request = new PaymentRequest(
                "test_payment_key",
                "test_order_id",
                50000,
                "CARD"
        );

        paymentService.createPaymentWithRequest(reservation, request);

        Payment payment = paymentRepository.findByPaymentKey("test_payment_key").orElseThrow();
        assertAll(
                () -> assertThat(payment.getPaymentKey()).isEqualTo("test_payment_key"),
                () -> assertThat(payment.getOrderId()).isEqualTo("test_order_id"),
                () -> assertThat(payment.getType()).isEqualTo("CARD"),
                () -> assertThat(payment.getTotalAmount()).isEqualTo(50000),
                () -> assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAYMENT_REQUESTED)
        );
    }

    @Test
    void createPendingPayment_shouldCreatePayment() {
        paymentService.createPendingPayment(reservation);

        Payment payment = paymentRepository.findByReservationId(reservation.getId()).orElseThrow();
        assertAll(
                () -> assertThat(payment.getReservation()).isEqualTo(reservation),
                () -> assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING_FOR_PAYMENT)
        );
    }

    @Test
    void updatePaymentWithConfirm_shouldUpdatePayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000,
                "DONE",
                "2025-05-28T20:48:23+09:00"
        );

        paymentService.updatePaymentWithConfirm(response);

        Payment updatedPayment = paymentRepository.findByPaymentKey("test_payment_key").orElseThrow();
        assertAll(
                () -> assertThat(updatedPayment.getStatus()).isEqualTo("DONE"),
                () -> assertThat(updatedPayment.getRequestedAt()).isEqualTo("2025-05-28T20:48:23+09:00"),
                () -> assertThat(updatedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.PAYMENT_CONFIRMED)
        );
    }

    @Test
    void updatePaymentWithConfirm_shouldThrowException_whenPaymentNotFound() {
        PaymentResponse response = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000,
                "DONE",
                "2025-05-28T20:48:23+09:00"
        );

        assertThatThrownBy(() -> paymentService.updatePaymentWithConfirm(response))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("요청한 paymentKey에 해당하는 결제가 없습니다.");
    }

    @Test
    void findByReservationId_shouldReturnPayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        paymentRepository.save(payment);

        Payment found = paymentService.findByReservationId(reservation.getId());

        assertAll(
                () -> assertThat(found.getPaymentKey()).isEqualTo("test_payment_key"),
                () -> assertThat(found.getOrderId()).isEqualTo("test_order_id"),
                () -> assertThat(found.getType()).isEqualTo("CARD"),
                () -> assertThat(found.getTotalAmount()).isEqualTo(50000)
        );
    }

    @Test
    void findByReservationId_shouldThrowException_whenPaymentNotFound() {
        assertThatThrownBy(() -> paymentService.findByReservationId(reservation.getId()))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("요청한 reservation_id에 해당하는 결제가 없습니다.");
    }

    @Test
    void deleteById_shouldDeletePayment() {
        Payment payment = Payment.createRequestedPayment(
                reservation,
                "test_payment_key",
                "test_order_id",
                "CARD",
                50000
        );
        payment = paymentRepository.save(payment);

        paymentService.deleteById(payment.getId());

        assertThat(paymentRepository.findByPaymentKey("test_payment_key")).isEmpty();
    }
} 