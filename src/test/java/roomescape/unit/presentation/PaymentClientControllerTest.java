package roomescape.unit.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
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
}
