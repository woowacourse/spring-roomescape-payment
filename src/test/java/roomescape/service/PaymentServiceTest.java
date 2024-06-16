package roomescape.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.BaseTest;
import roomescape.exception.PaymentException;
import roomescape.service.fixture.ReservationFixture;
import roomescape.service.fixture.ReservationRequestBuilder;
import roomescape.service.httpclient.TossPaymentRestTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@Sql(scripts = "/initialize_table.sql")
class PaymentServiceTest extends BaseTest {

    private PaymentService paymentService;
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Autowired
    private TossPaymentRestTemplate tossPaymentRestTemplate;

    @Autowired
    public PaymentServiceTest(final PaymentService paymentService, final RestTemplate restTemplate) {
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @DisplayName("등록된 금액과 다른 금액이 전달되었을 경우 예외를 발생한다.")
    @Test
    void should_throw_exception_when_different_amount() {
        assertThatThrownBy(() ->
                paymentService.confirmReservationPayments(ReservationRequestBuilder.builder().amount(1234L).build(), ReservationFixture.GENERAL.getReservation()))
                .isInstanceOf(PaymentException.class)
                .hasMessage("[ERROR] 클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [1234]");
    }

    @DisplayName("외부 API에서 4xx를 응답할 경우 예외를 발생한다.")
    @Test
    public void should_throw_exception_when_status_4xx() {
        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> paymentService.confirmReservationPayments(ReservationRequestBuilder.builder().build(), ReservationFixture.GENERAL.getReservation()))
                .isInstanceOf(PaymentException.class)
                .hasMessage("[ERROR] 결제에 실패했습니다.");
    }

    @DisplayName("외부 API에서 5xx를 응답 할 경우 예외를 발생한다.")
    @Test
    public void should_throw_exception_when_status_5xx() {
        mockServer.expect(anything())
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> paymentService.confirmReservationPayments(ReservationRequestBuilder.builder().build(), ReservationFixture.GENERAL.getReservation()))
                .isInstanceOf(PaymentException.class)
                .hasMessage("[ERROR] 결제 서버에 오류가 발생했습니다.");
    }
}
