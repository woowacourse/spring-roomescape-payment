package roomescape.infra.payment.toss;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.PaymentClient;
import roomescape.application.dto.request.PaymentConfirmApiRequest;
import roomescape.application.dto.response.PaymentConfirmApiResponse;
import roomescape.infra.payment.toss.exception.TossPaymentConfirmException;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final TossPaymentConfirmErrorHandler errorHandler;

    public TossPaymentClient(
            RestClient restClient,
            TossPaymentConfirmErrorHandler errorHandler
    ) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    @Override
    public PaymentConfirmApiResponse confirmPayment(PaymentConfirmApiRequest request) {
        PaymentConfirmApiResponse response = restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentConfirmApiResponse.class);

        if (response == null || response.isNotDone()) {
            throw new TossPaymentConfirmException("결제가 완료되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
