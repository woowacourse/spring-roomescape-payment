package roomescape.reservation.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.reservation.dto.PaymentConfirmRequest;

@Component
public class TossPaymentClient implements PaymentClient {
    private static final String CONFIRM_URI = "/v1/payments/confirm";
    private final RestClient restClient;

    public TossPaymentClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public void requestConfirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new IllegalRequestException("결제 요청에 대한 문제가 있습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new InternalServerException("결제 승인 도중 알 수 없는 예외가 발생했습니다");
                })
                .toBodilessEntity();
    }
}
