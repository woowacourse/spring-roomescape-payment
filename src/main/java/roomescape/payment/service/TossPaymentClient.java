package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.payment.dto.PaymentCancelRequest;
import roomescape.payment.dto.TossPaymentConfirmRequest;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String CONFIRM_URI = "/v1/payments/confirm";
    private static final String CANCEL_URI = "/v1/payments/%s/cancel";
    private final RestClient restClient;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public TossPaymentClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public void requestConfirmPayment(TossPaymentConfirmRequest tossPaymentConfirmRequest) {
        restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossPaymentConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.warn("결제 요청 문제 발생: {}", new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new IllegalRequestException("결제 요청에 대한 문제가 있습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new InternalServerException("결제 승인 도중 알 수 없는 예외가 발생했습니다");
                })
                .toBodilessEntity();
    }

    @Override
    public void cancelPayment(String paymentKey) {
        restClient.post()
                .uri(String.format(CANCEL_URI, paymentKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(PaymentCancelRequest.makePaymentCancelRequest())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("결제 취소 요청 문제 발생: {}", new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new IllegalRequestException("결제 취소 요청에 대한 문제가 있습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new InternalServerException("결제 취소 도중 알 수 없는 예외가 발생했습니다");
                })
                .toBodilessEntity();

    }
}
