package roomescape.payment.infrastructure;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentCancelRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.exception.TossPaymentException;

@Slf4j
public class TossRestClient {

    private final RestClient restClient;
    private final TossErrorHandler tossErrorHandler;
    private final String authHeaderValue;

    public TossRestClient(final RestClient restClient, final TossErrorHandler tossErrorHandler, final TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.tossErrorHandler = tossErrorHandler;
        this.authHeaderValue = "Basic " +
                Base64.getEncoder().encodeToString((tossPaymentProperties.getWidgetSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentResponse confirm(final TossPaymentRequest request) {
        return postWithErrorHandling("/v1/payments/confirm", request);
    }

    public TossPaymentResponse cancel(final String paymentKey, final TossPaymentCancelRequest request) {
        log.debug("cancel 중 🔥🔥🔥 paymentKey: {}", paymentKey);
        TossPaymentResponse cancelResponse = postWithErrorHandling("/v1/payments/{paymentKey}/cancel", request,
                paymentKey);
        validateCancelSuccess(cancelResponse, paymentKey);
        return cancelResponse;
    }

    private void validateCancelSuccess(TossPaymentResponse cancelResponse, String paymentKey) {
        if (cancelResponse.status().equals("CANCELED")) {
            return;
        }
        log.warn("결제 취소 실패 - status={}, paymentKey={}", cancelResponse.status(), paymentKey);
        throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소 실패", true);
    }

    private <T> TossPaymentResponse postWithErrorHandling(String uri, T requestBody, Object... uriVariables) {
        try {
            return restClient.post()
                    .uri(uri, uriVariables)
                    .header("Authorization", authHeaderValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            tossErrorHandler
                    )
                    .body(TossPaymentResponse.class);
        } catch (ResourceAccessException ex) {
            log.error("Resource Access Exception:", ex);
            if (ex.getCause() instanceof SocketTimeoutException) {
                log.warn("토스 API 요청 타임아웃");
                throw new PaymentTimeoutException("결제 시스템이 응답하지 않아 시간이 초과되었습니다.");
            }
            throw ex;
        }
    }
}
