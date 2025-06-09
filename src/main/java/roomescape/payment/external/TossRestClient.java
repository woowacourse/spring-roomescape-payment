package roomescape.payment.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.exception.PaymentException;

@Component("tossApiClient")
public class TossRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossRestClient.class);

    private final RestClient tossRestClient;

    public TossRestClient(@Qualifier("tossRestClient") RestClient tossRestClient) {
        this.tossRestClient = tossRestClient;
    }

    public PaymentResponseDto confirmPayment(PaymentRequestDto requestDto) {
        LOGGER.info("[TOSS 요청] 결제 승인 요청: paymentKey={}, orderId={}", requestDto.paymentKey(), requestDto.orderId());

        try {
            PaymentResponseDto response = tossRestClient.post()
                    .uri("/v1/payments/confirm")
                    .body(requestDto)
                    .retrieve()
                    .body(PaymentResponseDto.class);

            LOGGER.info("[TOSS 응답] 결제 승인 성공: paymentKey={}, orderId={}, amount={}",
                    response.paymentKey(), response.orderId(), response.totalAmount());
            return response;
        } catch (Exception e) {
            LOGGER.error("[TOSS 오류] 결제 승인 실패: paymentKey={}, orderId={}, 이유={}",
                    requestDto.paymentKey(), requestDto.orderId(), e.getMessage(), e);
            throw new PaymentException(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
