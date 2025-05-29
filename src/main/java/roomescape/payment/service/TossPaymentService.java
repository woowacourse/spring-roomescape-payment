package roomescape.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.payment.service.dto.ConfirmPaymentResponse;
import roomescape.payment.service.dto.PaymentFailure;

import java.util.Base64;
import java.util.List;

@Service
public class TossPaymentService {
    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH"
    );
    private static final String AUTHORIZATION_TYPE = "Basic";
    @Value("${api.toss.secret-key}")
    private String secretKey;

    private final RestClient restClient;
    private final PaymentRepository paymentRepository;

    public TossPaymentService(
            @Value("${api.toss.url}") String baseUrl,
            PaymentRepository paymentRepository
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.paymentRepository = paymentRepository;
    }

    // TODO: 결제 실패시 환불
    public ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        ConfirmPaymentResponse paymentResponse = restClient.post()
                .uri("/confirm")
                .header("Authorization", AUTHORIZATION_TYPE + " " + getEncodedSecretKey())
                .body(paymentRequest)
                .retrieve()
                .body(ConfirmPaymentResponse.class);

        handlePaymentResponse(paymentResponse);

        paymentRepository.save(paymentResponse.toEntity());
        return paymentResponse;
    }

    private String getEncodedSecretKey() {
        return Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    private void handlePaymentResponse(ConfirmPaymentResponse response) {
        if (response.failure() == null) {
            return ;
        }
        PaymentFailure failure = response.failure();
        if (IGNORE_CODES.contains(failure.code())) {
            throw new RuntimeException(failure.message());
        }
        throw new PaymentClientException(failure.message());
    }
}
