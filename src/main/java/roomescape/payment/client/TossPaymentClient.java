package roomescape.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.PaymentCancelRequest;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentCancelResponse;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.dto.response.TossPaymentErrorResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

public class TossPaymentClient implements PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        logPaymentInfo(paymentRequest);
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handlePaymentError(res))
                .body(PaymentResponse.class);
    }

    public PaymentCancelResponse cancelPayment(PaymentCancelRequest cancelRequest) {
        logPaymentCancelInfo(cancelRequest);
        Map<String, String> param = Map.of("cancelReason", cancelRequest.cancelReason());

        return restClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", cancelRequest.paymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(param)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        (req, res) -> handlePaymentError(res))
                .body(PaymentCancelResponse.class);
    }

    private void logPaymentInfo(PaymentRequest paymentRequest) {
        log.info("결제 승인 요청: paymentKey={}, orderId={}, amount={}, paymentType={}",
                paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount(),
                paymentRequest.paymentType());
    }

    private void logPaymentCancelInfo(PaymentCancelRequest cancelRequest) {
        log.info("결제 취소 요청: paymentKey={}, amount={}, cancelReason={}",
                cancelRequest.paymentKey(), cancelRequest.amount(), cancelRequest.cancelReason());
    }

    private void handlePaymentError(ClientHttpResponse res)
            throws IOException {
        HttpStatusCode statusCode = res.getStatusCode();
        ErrorType errorType = getErrorTypeByStatusCode(statusCode);
        TossPaymentErrorResponse errorResponse = getErrorResponse(res);

        throw new RoomEscapeException(errorType,
                String.format("[ErrorCode = %s, ErrorMessage = %s]", errorResponse.code(), errorResponse.message()),
                statusCode);
    }

    private TossPaymentErrorResponse getErrorResponse(ClientHttpResponse res) throws IOException {
        InputStream body = res.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        TossPaymentErrorResponse errorResponse = objectMapper.readValue(body, TossPaymentErrorResponse.class);
        body.close();
        return errorResponse;
    }

    private ErrorType getErrorTypeByStatusCode(HttpStatusCode statusCode) {
        if (statusCode.is4xxClientError()) {
            return ErrorType.PAYMENT_ERROR;
        }
        return ErrorType.PAYMENT_SERVER_ERROR;
    }
}
