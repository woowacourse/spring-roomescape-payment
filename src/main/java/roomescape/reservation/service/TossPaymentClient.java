package roomescape.reservation.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.global.exception.PaymentErrorResponse;
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
