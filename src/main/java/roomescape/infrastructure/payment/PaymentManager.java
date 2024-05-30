package roomescape.infrastructure.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.exception.PaymentException;
import roomescape.service.request.PaymentApproveDto;

import java.io.IOException;

@Component
public class PaymentManager {

    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentManager(PaymentAuthorizationGenerator paymentAuthorizationGenerator, RestClient restClient, ObjectMapper objectMapper) {
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void approve(PaymentApproveDto paymentApproveDto) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentApproveDto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handlePaymentError)
                .toBodilessEntity();
    }

    private void handlePaymentError(HttpRequest request, ClientHttpResponse response) throws IOException {
        RuntimeException exception = objectMapper.readValue(response.getBody(), RuntimeException.class);
        throw new PaymentException(response.getStatusCode(), exception.getMessage());
    }
}
