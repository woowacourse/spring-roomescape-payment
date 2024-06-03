package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.PaymentClient;
import roomescape.service.request.PaymentApproveAppRequest;
import roomescape.service.response.PaymentApproveSuccessAppResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final ObjectMapper objectMapper;
    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;

    public TossPaymentClient(ObjectMapper objectMapper, PaymentAuthorizationGenerator paymentAuthorizationGenerator,
                             RestClient.Builder restClient,
                             @Value("${payment.base-url}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = restClient.baseUrl(baseUrl).build();
    }

    public PaymentApproveSuccessAppResponse approve(PaymentApproveAppRequest paymentApproveAppRequest) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        return restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentApproveAppRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, handleError(RoomescapeErrorCode.PAYMENT_FAILED))
                .onStatus(HttpStatusCode::is5xxServerError, handleError(RoomescapeErrorCode.INTERNAL_SERVER_ERROR))
                .body(PaymentApproveSuccessAppResponse.class);
    }

    private ErrorHandler handleError(RoomescapeErrorCode errorCode) {
        return (request, response) -> {
            InputStream body = response.getBody();
            Throwable throwable = objectMapper.readValue(body, Throwable.class);
            throw new RoomescapeException(errorCode, throwable.getMessage(), throwable);
        };
    }
}
