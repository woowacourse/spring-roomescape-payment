package roomescape.domain.payment.pg;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.dto.PaymentConfirmRequest;
import roomescape.domain.payment.dto.PaymentConfirmResponse;
import roomescape.domain.payment.exception.PaymentConfirmClientFailException;
import roomescape.domain.payment.exception.PaymentConfirmServerFailException;

import static roomescape.domain.payment.config.PaymentApiUrl.PG_CONFIRM_API_URL;

public class TossPaymentGateway implements PaymentGateway {

    private final RestClient restClient;

    public TossPaymentGateway(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        return restClient.post()
                .uri(PG_CONFIRM_API_URL)
                .body(new PaymentConfirmRequest(orderId, amount, paymentKey))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PaymentConfirmClientFailException(response.getStatusText());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentConfirmServerFailException(response.getStatusText());
                })
                .body(PaymentConfirmResponse.class);
    }
}
