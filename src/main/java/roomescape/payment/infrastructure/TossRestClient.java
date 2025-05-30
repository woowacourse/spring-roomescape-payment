package roomescape.payment.infrastructure;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.custom.PaymentBadRequestException;
import roomescape.payment.exception.custom.PaymentClientErrorException;
import roomescape.payment.exception.custom.PaymentConnectionException;
import roomescape.payment.exception.custom.PaymentException;
import roomescape.payment.exception.custom.PaymentServerException;
import roomescape.payment.exception.custom.PaymentTimeoutException;
import roomescape.payment.exception.custom.PaymentUnauthorizedException;

@Slf4j
@Component
public class TossRestClient {

    private final RestClient restClient;
    private final String authHeaderValue;

    public TossRestClient(final RestClient restClient, final TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.authHeaderValue = "Basic " +
                Base64.getEncoder()
                        .encodeToString((tossPaymentProperties.getWidgetSecretKey() + ":")
                                .getBytes(StandardCharsets.UTF_8));
    }

    @Retryable(
            retryFor = { ResourceAccessException.class, HttpServerErrorException.class},
            noRetryFor = { HttpClientErrorException.class, UnknownHttpStatusCodeException.class },
            backoff = @Backoff(delay = 2_000, multiplier = 2)
    )
    public TossPaymentResponse confirm(final TossPaymentRequest tossPaymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authHeaderValue)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(tossPaymentRequest)
                .retrieve()
                .body(TossPaymentResponse.class);
    }

    @Recover
    public TossPaymentResponse recover(final ResourceAccessException ex, final TossPaymentRequest req) {
        Throwable cause = ex.getCause();
        if (cause instanceof SocketTimeoutException) {
            log.error("토스 결제 confirm 요청 타임아웃", ex);
            throw new PaymentTimeoutException("결제 시스템이 응답하지 않아 시간이 초과되었습니다.");
        }
        if (cause instanceof ConnectException) {
            log.error("토스 결제 confirm 요청 연결 실패", ex);
            throw new PaymentConnectionException("결제 시스템에 연결할 수 없습니다. 네트워크를 확인해주세요.");
        }
        throw ex;
    }

    @Recover
    public TossPaymentResponse recover(HttpServerErrorException ex, TossPaymentRequest req) {
        final HttpStatusCode status = ex.getStatusCode();
        log.error("토스 결제 서버 오류: {} {}", status.value(), status, ex);
        if (status.value() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            throw new PaymentServerException("결제 시스템에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
        throw new PaymentException("결제 처리 중 알 수 없는 서버 오류가 발생했습니다. 상태코드: " + status.value());
    }

    @Recover
    public TossPaymentResponse recover(HttpClientErrorException ex, TossPaymentRequest req) {
        HttpStatusCode status = ex.getStatusCode();
        log.error("토스 결제 클라이언트 오류: {} {}", status.value(), status, ex);
        if (status.value() == HttpStatus.UNAUTHORIZED.value()) {
            throw new PaymentUnauthorizedException("결제 인증에 실패했습니다.");
        }
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            throw new PaymentBadRequestException("잘못된 결제 요청입니다: " + ex.getResponseBodyAsString());
        }
        throw new PaymentClientErrorException("결제 요청이 거부되었습니다. 상태코드: " + status.value());
    }
}
