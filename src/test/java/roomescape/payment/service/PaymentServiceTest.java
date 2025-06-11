package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.infrastructure.PaymentClient;
import roomescape.payment.infrastructure.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import(PaymentService.class)
class PaymentServiceTest {

    @MockitoBean
    private PaymentClient paymentClient;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        PaymentResponse mockResponse = new PaymentResponse(
                "test_payment_key",
                "test_order_id",
                "NORMAL",
                50000,
                "DONE",
                OffsetDateTime.of(2025, 5, 28, 20, 48, 23, 0, ZoneOffset.UTC)
        );
        when(paymentClient.authPayment(any()))
                .thenReturn(mockResponse);
    }

    @Test
    void createPayment_test() {
        Reservation reservation = createReservation();
        PaymentRequest request = new PaymentRequest("test_payment_key", "test_order_id", 50000, "NORMAL");

        Payment payment = paymentService.createPayment(request, reservation);

        assertAll(
                () -> assertThat(payment.getId()).isNotNull(),
                () -> assertThat(payment.getPaymentKey()).isEqualTo("test_payment_key"),
                () -> assertThat(payment.getOrderId()).isEqualTo("test_order_id"),
                () -> assertThat(payment.getAmount()).isEqualTo(50000),
                () -> assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING)
        );
    }

    @Test
    void sendPaymentRequest_test() {
        PaymentRequest request = new PaymentRequest("test_payment_key", "test_order_id", 50000, "CARD");
        PaymentResponse expected = new PaymentResponse("test_payment_key", "test_order_id", "NORMAL",
                50000, "DONE", OffsetDateTime.of(2025, 5, 28, 20, 48, 23, 0, ZoneOffset.UTC));

        PaymentResponse paymentResponse = paymentService.sendPaymentRequest(request);

        assertThat(paymentResponse).isEqualTo(expected);
    }

    @Test
    void updatePaymentToFail_test() {
        Reservation reservation = createReservation();
        PaymentRequest request = new PaymentRequest("test_payment_key", "test_order_id", 50000, "NORMAL");
        Payment payment = paymentService.createPayment(request, reservation);

        paymentService.updatePaymentToFail(payment.getId());
        em.flush();
        em.clear();

        Payment updatePayment = em.find(Payment.class, payment.getId());
        assertThat(updatePayment.getStatus()).isEqualTo(PaymentStatus.FAIL);
    }

    @Test
    void updatePaymentToSuccess_test() {
        Reservation reservation = createReservation();
        PaymentRequest request = new PaymentRequest("test_payment_key", "test_order_id", 50000, "NORMAL");
        Payment payment = paymentService.createPayment(request, reservation);

        paymentService.updatePaymentToSuccess(payment.getId());
        em.flush();
        em.clear();

        Payment updatePayment = em.find(Payment.class, payment.getId());
        assertThat(updatePayment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    private Reservation createReservation() {
        Theme theme = Theme.of("a", "a", "a");
        ReservationTime reservationTime = ReservationTime.withUnassignedId(LocalTime.of(10, 0));
        Member member = new Member("member", "member@naver.com", "asd", MemberRole.USER);
        ReservationInfo reservationInfo = new ReservationInfo(LocalDate.now().plusDays(1), reservationTime, theme);
        em.persist(reservationTime);
        em.persist(theme);
        em.persist(member);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, reservationInfo);
        em.persist(reservation);
        return reservation;
    }
}