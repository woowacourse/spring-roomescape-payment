package roomescape.payment.infraStructure.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.payment.infraStructure.PaymentGatewayClient;
import roomescape.payment.infraStructure.dto.ConfirmPaymentRequest;
import roomescape.payment.infraStructure.dto.ConfirmPaymentResponse;
import roomescape.payment.infraStructure.dto.PaymentFailure;

import java.util.Base64;
import java.util.List;

public class TossPaymentClient implements PaymentGatewayClient {
    @Value("${payment.toss.secret-key}")
    private String secretKey;

    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY"
    );

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        ConfirmPaymentResponse paymentResponse = restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey+":").getBytes()))
                .body(paymentRequest)
                .retrieve()
                .body(ConfirmPaymentResponse.class);
        handlePaymentResponse(paymentResponse);
        return paymentResponse;
    }

    private void handlePaymentResponse(ConfirmPaymentResponse response) {
        PaymentFailure failure = response.failure();
        if (failure == null) {
            return ;
        }
        if (IGNORE_CODES.contains(failure.code())) {
            throw new RuntimeException("토프 결제 승인 API 요청 값이 올바르지 않습니다.");
        }
        throw new PaymentClientException(failure.message());
    }
}
