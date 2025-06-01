package roomescape.presentation;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.config.paymentResponseErrorHandler;
import roomescape.domain.PaymentInfo;
import roomescape.dto.request.PaymentRequest;

@Component
public class PaymentClientController {

    private static final List<String> CODES = List.of("INVALID_API_KEY", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT");

    private final RestClient restClient;
    private final paymentResponseErrorHandler paymentResponseErrorHandler;

    public PaymentClientController(final RestClient restClient,
                                   final paymentResponseErrorHandler paymentResponseErrorHandler) {
        this.restClient = restClient;
        this.paymentResponseErrorHandler = paymentResponseErrorHandler;
    }

    public PaymentInfo postPaymentInfo(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .onStatus(status ->
                                status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> paymentResponseErrorHandler.handleError(response,
                                response.getStatusCode()))
                .body(PaymentInfo.class);
    }
}
