package roomescape.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.request.RefundPaymentRequest;
import roomescape.dto.response.ConfirmPaymentResponse;
import roomescape.dto.response.PaymentErrorResponse;
import roomescape.dto.response.RefundPaymentResponse;
import roomescape.entity.Payment;
import roomescape.exception.custom.PaymentException;

/**
 * <a href="https://docs.tosspayments.com/reference">toss api 문서</a>
 */
@Component
public class TossPaymentClient implements PaymentClient {

    public static String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest paymentRequest) {
        String authorizations = getAuthorizationToken();

        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                            response.getBody().readAllBytes(),
                            PaymentErrorResponse.class);
                    if (TossErrorCode.containsCode(paymentErrorResponse.code())) {
                        throw new PaymentException("결제 오류: " + paymentErrorResponse.code());
                    }
                    throw new PaymentException(paymentErrorResponse.message());
                }))
                .toEntity(ConfirmPaymentResponse.class)
                .getBody();
    }

    @Override
    public RefundPaymentResponse refund(Payment payment) {
        String authorizations = getAuthorizationToken();
        RefundPaymentRequest refundPaymentRequest = new RefundPaymentRequest("사용자 결제 취소");

        return restClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(refundPaymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                            response.getBody().readAllBytes(),
                            PaymentErrorResponse.class);
                    if (TossErrorCode.containsCode(paymentErrorResponse.code())) {
                        throw new PaymentException("결제 오류: " + paymentErrorResponse.code());
                    }
                    throw new PaymentException(paymentErrorResponse.message());
                }))
                .toEntity(RefundPaymentResponse.class)
                .getBody();
    }

    private String getAuthorizationToken() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
