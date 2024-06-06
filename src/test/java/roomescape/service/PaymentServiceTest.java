package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.BasicAcceptanceTest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.dto.payment.PaymentRequest;

class PaymentServiceTest extends BasicAcceptanceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void SetUp() {
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '1' DAY , 1, 1, 1, 'RESERVATION')");
    }

    @DisplayName("결제 승인 후 응답 내역을 저장한다.")
    @Test
    void confirmPayment() {
        PaymentRequest paymentRequest = new PaymentRequest("orderId", BigDecimal.valueOf(1000), "paymentKey");
        Reservation reservation = reservationRepository.findById(1L).get();

        paymentService.confirmPayment(paymentRequest, reservation);
        Payment result = paymentRepository.findById(1L).get();

        assertAll(
                () -> assertThat(paymentRepository.count()).isEqualTo(1),
                () -> assertThat(result.getReservation()).isEqualTo(reservation),
                () -> assertThat(result.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(1000))
        );
    }

    @DisplayName("결제 내역을 삭제한다.")
    @Test
    void deletePayment() {
        PaymentRequest paymentRequest = new PaymentRequest("orderId", BigDecimal.valueOf(1000), "paymentKey");
        Reservation reservation = reservationRepository.findById(1L).get();
        paymentService.confirmPayment(paymentRequest, reservation);

        paymentService.deletePayment(1L);

        assertThat(paymentRepository.count()).isEqualTo(0);
    }
}
