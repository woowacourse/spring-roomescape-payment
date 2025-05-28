package roomescape.unit.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
import roomescape.exception.FilteredPaymentException;
import roomescape.presentation.PaymentClientController;

public class PaymentClientControllerTest {

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .defaultHeader("Authorization", String.format("%s %s", "Basic", Base64.getEncoder()));

    private MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();
    private PaymentClientController clientController = new PaymentClientController(testBuilder.build());

    @Test
    void 결제_요청_응답을_확인한다() throws Exception {
        //given
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentInfo paymentInfo = new PaymentInfo("1", 1000);
        String paymentInfoJson = objectMapper.writeValueAsString(paymentInfo);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(paymentInfoJson, MediaType.APPLICATION_JSON));

        //when
        PaymentRequest paymentRequest = new PaymentRequest(1000, "1", "10");
        PaymentInfo result = clientController.postPaymentInfo(paymentRequest);

        assertThat(paymentInfo).isEqualTo(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID_API_KEY", "UNAUTHORIZED_KEY", "INCORRECT_BASIC_AUTH_FORMAT"})
    void 필터링된_예외를_발생시킨다(String code) throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        Error error = new Error(code, "Empty");
        String errorJson = objectMapper.writeValueAsString(error);

        server.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        //when
        PaymentRequest paymentRequest = new PaymentRequest(1000, "1", "10");
        assertThatThrownBy(() -> clientController.postPaymentInfo(paymentRequest))
                .isInstanceOf(FilteredPaymentException.class)
                .hasMessage("결제가 실패했습니다. 고객센터로 문의해 주세요.");
    }

    record Error(String code, String message) {
    }
}
