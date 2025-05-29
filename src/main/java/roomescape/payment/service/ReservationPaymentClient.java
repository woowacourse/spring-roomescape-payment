package roomescape.payment.service;

import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.payment.service.dto.ConfirmPaymentResponse;
import roomescape.payment.service.dto.PaymentFailure;

import java.util.Base64;
import java.util.List;

public class ReservationPaymentClient {
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY"
    );

    private final RestClient restClient;

    public ReservationPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        ConfirmPaymentResponse paymentResponse = restClient.post()
                .uri("/confirm")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY+":").getBytes()))
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
