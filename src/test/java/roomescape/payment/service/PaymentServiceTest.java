package roomescape.payment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.exception.PaymentRequestException;
import roomescape.payment.infrastructure.JpaPaymentRepository;
import roomescape.payment.infrastructure.JpaPaymentRepositoryAdapter;
import roomescape.payment.infrastructure.dto.PaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Status;
import roomescape.reservation.infrastructure.JpaReservationRepository;
import roomescape.reservation.infrastructure.JpaReservationRepositoryAdapter;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.member.domain.Member;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@Import(PaymentServiceTest.PaymentConfig.class)
class PaymentServiceTest {

    private static final String PAYMENT_KEY = "tgen_20240513184816ZSAZ9";
    private static final String ORDER_ID = "MC4wNDYzMzA0OTc2MDgy";
    private static final int AMOUNT = 1000;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("결제 정보를 저장할 수 있다.")
    @Test
    void can_save_payment() {
        ReservationTime time = ReservationTime.createWithoutId(LocalTime.of(10, 0));
        Theme theme = Theme.createWithoutId("테마1", "테마1 설명", "테마1 썸네일");
        Member member = Member.createWithoutId("테스터", "test@test.com", "password");

        Reservation reservation = Reservation.createWithoutId(
            LocalDate.of(2025, 4, 29),
            time,
            theme,
            member,
            Status.RESERVED
        );
        Reservation savedReservation = reservationRepository.save(reservation);

        PaymentRequest request = new PaymentRequest(PAYMENT_KEY, ORDER_ID, AMOUNT);

        Payment savedPayment = paymentService.savePayment(savedReservation, request);

        assertThat(savedPayment.getPaymentKey()).isEqualTo(PAYMENT_KEY);
        assertThat(savedPayment.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(savedPayment.getAmount()).isEqualTo(AMOUNT);
    }

    @DisplayName("예약 ID로 결제 정보를 조회할 수 있다.")
    @Test
    void can_find_payment_by_reservation_id() {
        Long reservationId = 1L;

        Payment foundPayment = paymentService.findByReservationId(reservationId);

        assertThat(foundPayment.getPaymentKey()).isEqualTo("abcd");
        assertThat(foundPayment.getOrderId()).isEqualTo("주문1");
        assertThat(foundPayment.getAmount()).isEqualTo(1000);
    }

    @DisplayName("존재하지 않는 예약 ID로 결제 정보를 조회하면 예외가 발생한다.")
    @Test
    void cannot_find_payment_by_non_existent_reservation_id() {
        Long nonExistentReservationId = 999L;

        Assertions.assertThatThrownBy(() -> paymentService.findByReservationId(nonExistentReservationId))
            .isInstanceOf(PaymentRequestException.class)
            .hasMessage("결제 정보를 찾을 수 없습니다.");
    }

    static class PaymentConfig {
        @Bean
        public PaymentRepository paymentRepository(JpaPaymentRepository jpaPaymentRepository) {
            return new JpaPaymentRepositoryAdapter(jpaPaymentRepository);
        }

        @Bean
        public PaymentClient paymentClient() {
            return mock(PaymentClient.class);
        }

        @Bean
        public PaymentService paymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
            return new PaymentService(paymentClient, paymentRepository);
        }

        @Bean
        public ReservationRepository reservationRepository(JpaReservationRepository jpaReservationRepository) {
            return new JpaReservationRepositoryAdapter(jpaReservationRepository);
        }
    }
}