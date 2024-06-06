package roomescape.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
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

    private void logPaymentInfo(PaymentRequest paymentRequest) {
        log.error("결제 승인 요청: paymentKey={}, orderId={}, amount={}, paymentType={}",
                paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount(),
                paymentRequest.paymentType());
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
