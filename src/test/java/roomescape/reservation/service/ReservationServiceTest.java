package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.PaymentException;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Role;
import roomescape.payment.controller.dto.PaymentConfirmWebRequest;
import roomescape.payment.service.dto.PaymentConfirmResponse;
import roomescape.payment.service.usecase.PaymentRestClient;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWithPaymentWebRequest;
import roomescape.reservation.domain.Reservation;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationService reservationService;

    @MockitoBean
    private PaymentRestClient paymentRestClient;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(
                "INSERT INTO member (id, name, email, role) VALUES (?, ?, ?, ?)",
                1L, "member1", "11@gmail.com", "USER"
        );
        jdbcTemplate.update(
                "INSERT INTO theme (id, name, description, thumbnail) VALUES (?, ?, ?, ?)",
                1L, "테마1", "테마1 설명", "www.theme1.com"
        );

        jdbcTemplate.update(
                "INSERT INTO reservation_time (id, start_at) VALUES (?, ?)",
                1L, LocalTime.of(14, 30)
        );
    }

    @Nested
    class create {

        private static final CreateReservationWithPaymentWebRequest REQUEST = new CreateReservationWithPaymentWebRequest(
                new CreateReservationWebRequest(
                        LocalDate.of(2025, 10, 15), 1L, 1L
                ),
                new PaymentConfirmWebRequest("paymentKey", "orderId", 1000)
        );

        private static final MemberInfo MEMBER_INFO = new MemberInfo(1L, "name", "email", Role.USER);

        private static final PaymentConfirmResponse PAYMENT_CONFIRM_RESPONSE = new PaymentConfirmResponse(
                "paymentKey",
                "orderId",
                1000,
                "success"
        );

        @DisplayName("결제 승인 요청을 성공하면 예약을 생성한다.")
        @Test
        void paymentConfirmSuccess() {
            // given
            when(paymentRestClient.confirm(REQUEST.paymentConfirmWebRequest().toPaymentConfirmRequest(), 1L))
                    .thenReturn(PAYMENT_CONFIRM_RESPONSE);

            // when
            reservationService.paymentConfirmAndCreate(REQUEST, MEMBER_INFO);

            // then
            final List<Reservation> actual = entityManager
                    .createQuery("SELECT r FROM Reservation r", Reservation.class)
                    .getResultList();

            assertThat(actual).hasSize(1);
        }

        @DisplayName("결제 승인 요청에 실패하면 예약을 생성하지 않는다.")
        @Test
        void paymentConfirmFailed() {
            // given
            when(paymentRestClient.confirm(REQUEST.paymentConfirmWebRequest().toPaymentConfirmRequest(), 1L))
                    .thenThrow(new PaymentException(HttpStatus.FORBIDDEN, "결제 승인 실패", "FAILED"));

            // when & then
            assertThatThrownBy(() -> reservationService.paymentConfirmAndCreate(REQUEST, MEMBER_INFO))
                    .isInstanceOf(PaymentException.class);

            final List<Reservation> actual = entityManager
                    .createQuery("SELECT r FROM Reservation r", Reservation.class)
                    .getResultList();

            assertThat(actual).isEmpty();
        }
    }
}
