package roomescape.domain.payment;

import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.Member;
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

    public Payment approve(PaymentApproveRequest paymentApproveRequest, Member member) {
        String encryptedKey = Base64.getEncoder().encodeToString(approveSecretKey.getBytes());
        ApproveApiResponse response = Optional.ofNullable(restClient.post()
                        .uri("/v1/payments/confirm")
                        .header("Authorization", "Basic " + encryptedKey)
                        .body(paymentApproveRequest)
                        .retrieve()
                        .onStatus(errorHandler)
                        .body(ApproveApiResponse.class))
                .orElseThrow(() -> new ApiCallException("알 수 없는 오류가 발생했습니다."));
        return new Payment(response.orderId(), response.paymentKey(), response.amount());
    }
}
