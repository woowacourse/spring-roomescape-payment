package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.advice.exception.RoomEscapeException;
import roomescape.config.BeanConfiguration;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@RestClientTest
@ContextConfiguration(classes = {BeanConfiguration.class})
class PaymentRestClientTest {

    private static final PaymentCreateRequest PAYMENT_CREATE_REQUEST = new PaymentCreateRequest(
            "tgen_20240528211", "MC40MTMwMTk0ODU0ODU4", BigDecimal.valueOf(1000),
            new Reservation(1L, MemberFixture.MEMBER_BRI, LocalDate.now().plusDays(1),
                    TimeFixture.TIME_1, ThemeFixture.THEME_1, ReservationStatus.RESERVED));

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PaymentRestClient paymentRestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @DisplayName("결제에 성공한다.")
    @Test
    void approvePaymentTest() throws JsonProcessingException {
        // given
        String request = objectMapper.writeValueAsString(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest());

        mockServer.expect(ExpectedCount.manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentRestClient.getSecretKey()))
                .andExpect(content().json(request))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // when & then
        assertThatCode(() -> paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST))
                .doesNotThrowAnyException();
    }

    @DisplayName("토스에서 에러를 응답하면 예외를 발생한다.")
    @Test
    void approvePaymentTest_whenTossErrorResponse() throws JsonProcessingException {
        // given
        String request = objectMapper.writeValueAsString(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest());
        String response = """
                {
                  "code": "EXCEED_MAX_PAYMENT_AMOUNT",
                  "message": "하루 결제 가능 금액을 초과했습니다."
                }
                """;

        mockServer.expect(ExpectedCount.manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentRestClient.getSecretKey()))
                .andExpect(content().json(request))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("하루 결제 가능 금액을 초과했습니다.")
                .extracting(exception -> ((RoomEscapeException) exception).getStatus())
                .isEqualTo(400);
    }

    @DisplayName("현재 서버의 문제로 인해 토스에서 에러를 응답하는 경우 500 예외로 전환한다.")
    @ParameterizedTest
    @MethodSource("provideCurrentServerErrorResponse")
    void approvePaymentTest_whenTossErrorResponseBecauseOfCurrentServer(String response, HttpStatus responseStatus)
            throws JsonProcessingException {
        // given
        String request = objectMapper.writeValueAsString(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest());

        mockServer.expect(ExpectedCount.manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentRestClient.getSecretKey()))
                .andExpect(content().json(request))
                .andRespond(withStatus(responseStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.")
                .extracting(exception -> ((RoomEscapeException) exception).getStatus())
                .isEqualTo(500);
    }

    private static Stream<Arguments> provideCurrentServerErrorResponse() {
        return Stream.of(
                Arguments.of("""
                        {
                          "code": "INVALID_ORDER_ID",
                          "message": "`orderId`는 영문 대소문자, 숫자, 특수문자(-, _) 만 허용합니다. 6자 이상 64자 이하여야 합니다."
                        }
                        """, HttpStatus.BAD_REQUEST),
                Arguments.of("""
                        {
                          "code": "INVALID_API_KEY",
                          "message": "잘못된 시크릿키 연동 정보 입니다."
                        }
                        """, HttpStatus.BAD_REQUEST),
                Arguments.of("""
                        {
                          "code": "UNAUTHORIZED_KEY",
                          "message": "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."
                        }
                        """, HttpStatus.UNAUTHORIZED),
                Arguments.of("""
                        {
                          "code": "INCORRECT_BASIC_AUTH_FORMAT",
                          "message": "잘못된 요청입니다. ':' 를 포함해 인코딩해주세요."
                        }
                        """, HttpStatus.FORBIDDEN)
        );
    }

    @DisplayName("토스 문서와 다른 JSON 에러가 응답되는 경우 500 예외로 전환한다.")
    @ParameterizedTest
    @MethodSource("provideIllegalTossErrorResponse")
    void approvePaymentTest_whenIllegalErrorResponse(String response, HttpStatus responseStatus)
            throws JsonProcessingException {
        // given
        String request = objectMapper.writeValueAsString(PAYMENT_CREATE_REQUEST.createRestClientPaymentApproveRequest());

        mockServer.expect(ExpectedCount.manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Basic " + paymentRestClient.getSecretKey()))
                .andExpect(content().json(request))
                .andRespond(withStatus(responseStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response));

        // when & then
        assertThatThrownBy(() -> paymentRestClient.approvePayment(PAYMENT_CREATE_REQUEST))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("서버에 문제가 발생해 결제가 실패했습니다. 관리자에게 문의해 주세요.")
                .extracting(exception -> ((RoomEscapeException) exception).getStatus())
                .isEqualTo(500);
    }

    private static Stream<Arguments> provideIllegalTossErrorResponse() {
        return Stream.of(
                Arguments.of("""
                        {}
                        """, HttpStatus.BAD_REQUEST),
                Arguments.of("""
                        {
                          "code": "INVALID_API_KEY",
                        }
                        """, HttpStatus.BAD_REQUEST),
                Arguments.of("""
                        {
                          "message": "게이트웨이 또는 프록시가 시간 초과되었습니다."
                        }
                        """, HttpStatus.GATEWAY_TIMEOUT),
                Arguments.of("""
                        {
                          "status": "BAD_REQUEST",
                          "detail": "잘못된 요청입니다."
                        }
                        """, HttpStatus.BAD_REQUEST)
        );
    }
}
