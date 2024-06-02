package roomescape.payment.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentConfirmFailException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Component
public class TossPaymentGateway implements PaymentGateway {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final RestClient restClient;

    public TossPaymentGateway(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentConfirmResponse confirm(
            final String orderId,
            final Long amount,
            final String paymentKey
    ) {
        PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(orderId, amount, paymentKey);
        try {
            return restClient
                    .post()
                    .uri("/confirm")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("TossPayments-Test-Code", "NOT_AVAILABLE_BANK")
                    .body(confirmRequest)
                    .retrieve()
                    .body(PaymentConfirmResponse.class);
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage(), e);
            throw new PaymentConfirmFailException(e.getMessage(), (HttpStatus) e.getStatusCode());
        }
    }
}
