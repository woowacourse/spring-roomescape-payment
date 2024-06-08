package roomescape.infrastructure.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.SocketTimeoutException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.TossPaymentErrorCode;
import roomescape.exception.TossPaymentException;

@Component
@Profile("!local")
public class TossPaymentClient implements PaymentClient {

    private static final Logger logger = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(PaymentRequest request) {
        try {
            String timestamp = Instant.now().toString();
            logger.debug("Request body: {} Request time: {}", request, timestamp);

            return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Payment.class);
        } catch (RestClientResponseException re) {
            logger.error("[토스 결제 에러] {}", re.getResponseBodyAsString(), re);
            TossErrorResponse error = re.getResponseBodyAs(TossErrorResponse.class);
            throw new TossPaymentException(error, (HttpStatus) re.getStatusCode());
        } catch (ResourceAccessException re) {
            logger.error(re.getMessage(), re);
            if (re.getCause() instanceof SocketTimeoutException) {
                if (re.getMessage().contains("Connect timed out")) {
                    throw new TossPaymentException(TossPaymentErrorCode.CONNECT_TIMEOUT);
                }
                if (re.getMessage().contains("Read timed out")) {
                    throw new TossPaymentException(TossPaymentErrorCode.READ_TIMEOUT);
                }
            }
            throw new TossPaymentException(TossPaymentErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("[토스 결제 에러] {}", e.getMessage(), e);
            throw new TossPaymentException(TossPaymentErrorCode.PAYMENT_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
