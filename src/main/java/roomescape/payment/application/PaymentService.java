package roomescape.payment.application;

import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

@Service
public class PaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String PAYMENTS_CONFIRM_API = "/v1/payments/confirm";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final String COLON = ":";

    private final RestClient restClient;
    private final PaymentApproveExceptionHandler paymentApproveExceptionHandler;

    public PaymentService(final RestClient restClient, final PaymentApproveExceptionHandler paymentApproveExceptionHandler) {
        this.restClient = restClient;
        this.paymentApproveExceptionHandler = paymentApproveExceptionHandler;
    }

    public PaymentApproveResponse approvePayment(final PaymentApproveRequest paymentApproveRequest) {
        return restClient.post()
                .uri(PAYMENTS_CONFIRM_API)
                .header(AUTHORIZATION, BASIC + toBase64(SECRET_KEY + COLON))
                .body(paymentApproveRequest)
                .retrieve()
                .onStatus(paymentApproveExceptionHandler)
                .body(PaymentApproveResponse.class);
    }

    private String toBase64(String rawText) {
        return Base64.getEncoder().encodeToString(rawText.getBytes());
    }
}
