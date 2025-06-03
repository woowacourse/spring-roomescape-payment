package roomescape.payment.processor.toss;

import java.util.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.when;

class TossPaymentProcessorTest {

    private static final String SECRET_KEY = "SecretKey";
    private static final String ENCODED_SECRET_KEY = Base64.getEncoder()
            .encodeToString((SECRET_KEY + ":base64").getBytes());

    private final TossPaymentProcessor paymentProcessor;

    private final RestClient mockTestRestClient;

    public TossPaymentProcessorTest() {
        mockTestRestClient = Mockito.mock(RestClient.class);
        paymentProcessor = new TossPaymentProcessor(SECRET_KEY, mockTestRestClient);
    }

    @Test
    void 토스_결제_요청에_따른_반환_확인() {
        // given
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                10000,
                "orderId",
                "paymentKey"
        );
        final TossPaymentConfirmResponse expected = new TossPaymentConfirmResponse(
                "orderId",
                "paymentKey"
        );

        RestClient.RequestBodyUriSpec uriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        when(mockTestRestClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri("/confirm")).thenReturn(bodySpec);
        when(bodySpec.header(HttpHeaders.AUTHORIZATION, "Basic " + ENCODED_SECRET_KEY)).thenReturn(bodySpec);
        when(bodySpec.body(request)).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(TossPaymentConfirmResponse.class)).thenReturn(expected);

        // when
        final TossPaymentConfirmResponse actual = paymentProcessor.processPayment(request);

        // then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
