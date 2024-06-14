package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.fixture.MemberFixture;
import roomescape.payment.domain.PaymentRestClient;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.dto.RestClientPaymentApproveResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.repository.TimeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@AutoConfigureMockMvc
class PaymentServiceTest {
    private static final Reservation RESERVATION = new Reservation(1L,
            MemberFixture.MEMBER_BRI, LocalDate.now().plusDays(1), TIME_1, THEME_1,
            ReservationStatus.RESERVED);
    private static final PaymentCreateRequest PAYMENT_CREATE_REQUEST = new PaymentCreateRequest("paymentKey",
            "orderId", BigDecimal.valueOf(1000), RESERVATION);
    private static final RestClientPaymentApproveResponse RESPONSE = new RestClientPaymentApproveResponse(
            "paymentKey", "orderId", BigDecimal.valueOf(1000), ZonedDateTime.now().plusDays(1)
    );

    @LocalServerPort
    private int port;
    @MockBean
    private PaymentRestClient paymentRestClient;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private TimeRepository timeRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        themeRepository.save(THEME_1);
        timeRepository.save(TIME_1);
        reservationRepository.save(RESERVATION);
    }

    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        when(paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest()))
                .thenReturn(RESPONSE);
        paymentService.approvePayment(PAYMENT_CREATE_REQUEST);

        assertThat(paymentService.findPaymentByReservation(RESERVATION).get().getId())
                .isEqualTo(1L);
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        when(paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest()))
                .thenReturn(RESPONSE);
        paymentService.approvePayment(PAYMENT_CREATE_REQUEST);

        doNothing().when(paymentRestClient).cancelPayment(PAYMENT_CREATE_REQUEST.paymentKey());
        paymentService.cancelPayment(RESERVATION.getId());

        assertThat(paymentService.findPaymentByReservation(RESERVATION))
                .isEmpty();
    }
}
