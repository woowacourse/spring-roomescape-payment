package roomescape.domain.payment;

import static roomescape.exception.ExceptionType.PAYMENT_FAIL_CAUSE_HIDDEN;

import java.time.Duration;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.dto.ApproveApiResponse;
import roomescape.dto.PaymentApproveRequest;
import roomescape.exception.RoomescapeException;

@Component
public class PaymentClient {
    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PaymentApiResponseErrorHandler errorHandler;
    @Value("${payment.approve.key}")
    private String approveSecretKey;

    public PaymentClient(PaymentApiResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(3));

        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .requestFactory(requestFactory)
                .build();
    }

    public ApproveApiResponse approve(PaymentApproveRequest approveRequest) {
        logger.info("payment API call Request = {}", approveRequest);
        try {
            return approveInternal(approveRequest);
        } catch (RestClientException e) {
            logger.error("payment API call ERROR!!!");
            throw new RoomescapeException(PAYMENT_FAIL_CAUSE_HIDDEN, e);
        }
    }

    private ApproveApiResponse approveInternal(PaymentApproveRequest paymentApproveRequest)
            throws ResourceAccessException {
        String encryptedKey = Base64.getEncoder().encodeToString(approveSecretKey.getBytes());
        ApproveApiResponse response = restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encryptedKey)
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(ApproveApiResponse.class);
        logger.info("payment API call Response = {}", response);
        return response;
    }
}
