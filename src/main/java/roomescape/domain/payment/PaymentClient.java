package roomescape.domain.payment;

import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.Member;
import roomescape.dto.PaymentApproveRequest;

@Component
public class PaymentClient {
    public static final String APPROVE_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .build();
    private final PaymentApiResponseErrorHandler errorHandler;

    public PaymentClient(PaymentApiResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public Payment approve(PaymentApproveRequest paymentApproveRequest, Member member) {
        String encryptedKey = Base64.getEncoder().encodeToString(APPROVE_SECRET_KEY.getBytes());
        ApproveApiResponse response = restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + encryptedKey)
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(ApproveApiResponse.class);
        return new Payment(response.orderId(), response.paymentKey(), response.amount(), member);
    }
}
