package roomescape.payment.client.toss;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.ClientException;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.client.PaymentProperties;
import roomescape.payment.client.PaymentRestClientConfiguration;
import roomescape.payment.dto.request.ConfirmPaymentRequest;

@RestClientTest({PaymentRestClientConfiguration.class, PaymentProperties.class})
@MockBean(JpaMetamodelMappingContext.class)
class TossRestClientTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PaymentClient paymentClient;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @BeforeEach
    void setUp() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        paymentClient = new TossPaymentClient(restClientBuilder.build());
    }

    @Test
    @DisplayName("토스 결제 승인 실패: 유저에게 알리지 않을 사유라면 디폴트 메시지 전달")
    void confirm_WhenTossErrorCodeForUser() throws JsonProcessingException {
        ConfirmPaymentRequest confirmPaymentRequest = new ConfirmPaymentRequest("paymetKey", "orderId", 100L);

        // stub
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                        .andRespond(withServerError().body(objectMapper.writeValueAsString(new TossClientErrorResponse("UNAUTHORIZED_KEY", "인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다."))));

        assertThatThrownBy(() -> paymentClient.confirm(confirmPaymentRequest))
                .isInstanceOf(ClientException.class)
                .hasMessage("결제 오류입니다. 같은 문제가 반복된다면 문의해주세요.");
    }

    @Test
    @DisplayName("토스 결제 승인 실패: 유저에게 알릴 사유라면 토스 에러 메시지 전달")
    void confirm_WhenTossErrorCodeNotForUser() throws JsonProcessingException {
        ConfirmPaymentRequest confirmPaymentRequest = new ConfirmPaymentRequest("paymetKey", "orderId", 100L);

        // stub
        mockRestServiceServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withServerError().body(objectMapper.writeValueAsString(new TossClientErrorResponse("EXCEED_MAX_ONE_DAY_AMOUNT", "일일 한도를 초과했습니다."))));

        assertThatThrownBy(() -> paymentClient.confirm(confirmPaymentRequest))
                .isInstanceOf(ClientException.class)
                .hasMessage("일일 한도를 초과했습니다.");
    }
}
