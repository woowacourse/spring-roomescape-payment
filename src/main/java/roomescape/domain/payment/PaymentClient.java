package roomescape.domain.payment;

import static roomescape.domain.payment.PaymentApiErrorCode.UNKNOWN;

import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentApproveRequest;

@Component
public class PaymentClient {
    public static final PaymentApiError UNKNOWN_API_ERROR = new PaymentApiError(UNKNOWN,
            "결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요.");
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(
                    Duration.ofMillis(3)).withReadTimeout(Duration.ofMillis(3))))
            .build();
    private final PaymentApiResponseErrorHandler errorHandler;
    @Value("${payment.approve.key}")
    private String approveSecretKey;

    public PaymentClient(PaymentApiResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public Payment approve(PaymentApproveRequest paymentApproveRequest) {
        try {
            return approveInternal(paymentApproveRequest);
        } catch (ResourceAccessException e) {
            throw new ApiCallException(UNKNOWN_API_ERROR, e);
        }
    }

    private Payment approveInternal(PaymentApproveRequest paymentApproveRequest) throws ResourceAccessException {
        String encryptedKey = Base64.getEncoder().encodeToString(approveSecretKey.getBytes());
        ApproveApiResponse response = restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encryptedKey)
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(ApproveApiResponse.class);
        validateNullResponse(response);
        return new Payment(response.orderId(), response.paymentKey(), response.amount());
    }

    private static void validateNullResponse(ApproveApiResponse response) {
        if (response == null) {
            throw new ApiCallException(UNKNOWN_API_ERROR);
        }
    }
}
