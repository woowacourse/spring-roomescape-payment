package roomescape.payment.infrastructure;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;

@RequiredArgsConstructor
@Slf4j
public class TossRestClient {

    private final RestClient restClient;
    private final String authHeaderValue;

    public TossRestClient(final RestClient restClient, final TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.authHeaderValue = "Basic " +
                Base64.getEncoder().encodeToString((tossPaymentProperties.getWidgetSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentResponse confirm(final TossPaymentRequest tossPaymentRequest) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", authHeaderValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(tossPaymentRequest)
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (ResourceAccessException ex) {
            if (ex.getCause() instanceof SocketTimeoutException) {
                log.error("토스 결제 confirm 요청 타임아웃", ex);
                throw new PaymentTimeoutException("결제 시스템이 응답하지 않아 시간이 초과되었습니다.");
            }
            throw ex;
        }
    }
}
