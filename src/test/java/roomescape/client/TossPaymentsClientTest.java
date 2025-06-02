package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.client.dto.TossErrorResponse;
import roomescape.global.exception.custom.TossPaymentsException;

class TossPaymentsClientTest {

    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final RestClient.Builder TEST_BUILDER = RestClient.builder()
            .baseUrl(BASE_URL);
    private static final MockRestServiceServer SERVER = MockRestServiceServer
            .bindTo(TEST_BUILDER)
            .build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TossPaymentsClient paymentsClient = new TossPaymentsClient(TEST_BUILDER.build(), MAPPER);

    @BeforeEach
    void setUp() {
        SERVER.reset();
    }

    @DisplayName("결제 승인 요청을 처리한다.")
    @Test
    void testConfirmPayments() throws JsonProcessingException {
        // given
        PaymentsConfirmRequest request = new PaymentsConfirmRequest("aaa", "111", 1000L);
        PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("aaa", 1000L);
        SERVER.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(MAPPER.writeValueAsString(expectedResponse))
                        .contentType(MediaType.APPLICATION_JSON));
        // when
        PaymentsConfirmResponse actualResponse = paymentsClient.confirmPayments(request);
        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("에러가 발생하면 커스텀 예외를 준다.")
    @Test
    void testConfirmPaymentsException() throws JsonProcessingException {
        // given
        PaymentsConfirmRequest request = new PaymentsConfirmRequest("aaa", "111", 1000L);
        TossErrorResponse errorResponse = new TossErrorResponse("잘못된 요청입니다.", "TEST_ERROR_CODE");
        SERVER.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .body(MAPPER.writeValueAsString(errorResponse))
                        .contentType(MediaType.APPLICATION_JSON));
        // when
        // then
        assertThatThrownBy(() -> paymentsClient.confirmPayments(request))
                .isInstanceOf(TossPaymentsException.class)
                .hasMessage(errorResponse.message());
    }

    @DisplayName("특정 예외는 사용자에게 정해진 메시지를 전달한다.")
    @Test
    void testConfirmPaymentsExceptionWithMaskedErrorCodes() throws JsonProcessingException {
        // given
        PaymentsConfirmRequest request = new PaymentsConfirmRequest("aaa", "111", 1000L);
        TossErrorResponse tossErrorResponse = new TossErrorResponse("잘못된 요청입니다.", "INVALID_REQUEST");
        // when
        SERVER.expect(requestTo(CONFIRM_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest()
                        .body(MAPPER.writeValueAsString(tossErrorResponse))
                        .contentType(MediaType.APPLICATION_JSON));
        // then
        assertThatThrownBy(() -> paymentsClient.confirmPayments(request))
                .isInstanceOf(TossPaymentsException.class)
                .hasMessage("결제에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
}
