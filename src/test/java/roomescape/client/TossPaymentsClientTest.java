package roomescape.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static roomescape.fixture.ServerClientFixture.BASE_URL;
import static roomescape.fixture.ServerClientFixture.MAPPER;
import static roomescape.fixture.ServerClientFixture.SERVER;
import static roomescape.fixture.ServerClientFixture.TEST_BUILDER;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.client.dto.TossErrorResponse;
import roomescape.global.exception.custom.TossPaymentsException;

class TossPaymentsClientTest {

    private final TossPaymentsClient paymentsClient = new TossPaymentsClient(TEST_BUILDER.build());

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
        SERVER.expect(requestTo(BASE_URL + "/confirm"))
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
        TossErrorResponse errorResponse = new TossErrorResponse("잘못된 요청입니다.");
        SERVER.expect(requestTo(BASE_URL + "/confirm"))
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
}
