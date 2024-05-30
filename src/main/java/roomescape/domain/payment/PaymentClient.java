package roomescape.domain.payment;

import static roomescape.domain.payment.PaymentApiErrorCode.UNKNOWN;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentApproveRequest;

@Component
public class PaymentClient {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .build();
    private final PaymentApiResponseErrorHandler errorHandler;
    @Value("${payment.approve.key}")
    private String approveSecretKey;

    public PaymentClient(PaymentApiResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public Payment approve(PaymentApproveRequest paymentApproveRequest) {
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
            throw new ApiCallException(new PaymentApiError(UNKNOWN, "결제를 진행할 수 없습니다. 고객 센터로 문의해 주세요."));
        }
    }
}
