package roomescape.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentClient;
import roomescape.dto.PaymentErrorResponse;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.PaymentException;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    @Value("${security.api.toss.secret-key}")
    private String widgetSecretKey;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        String authorizations = "Basic " + new String(
                encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
        try {
            return restClient.post()
                    .uri("v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            PaymentErrorResponse errorResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            TossPaymentExceptionType tossPaymentExceptionType = TossPaymentExceptionType.findBy(errorResponse.code());
            throw new PaymentException(tossPaymentExceptionType.getHttpStatus(), tossPaymentExceptionType.getMessage(),
                    e);
        }
    }
}
