package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentException;
import roomescape.service.payment.dto.PaymentErrorResult;
import roomescape.service.payment.dto.PaymentResult;
import roomescape.service.reservation.dto.ReservationRequest;

public class PaymentRestClient {
    @Value("${tosspay.secret_key}")
    private String secretKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentRestClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentResult confirm(ReservationRequest request) {
        HttpHeaders headers = generateHttpHeaders();
        Map<String, Object> amount = Map.of(
                "amount", request.amount(),
                "orderId", request.orderId(),
                "paymentKey", request.paymentKey()
        );

        try {
            return restClient.post()
                    .uri("/confirm")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(amount)
                    .retrieve()
                    .body(PaymentResult.class);
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new PaymentException(parseErrorBody(responseBody), e);
        }
    }

    public void cancel(Payment payment) {
        HttpHeaders headers = generateHttpHeaders();
        Map<String, String> cancelReason = Map.of("cancelReason", "고객이 취소를 원함");

        try {
            restClient.post()
                    .uri(String.format("/%s/cancel", payment.getPaymentKey()))
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(cancelReason)
                    .retrieve();
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new PaymentException(parseErrorBody(responseBody), e);
        }
    }

    private HttpHeaders generateHttpHeaders() {
        String header = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + header);
        return headers;
    }

    private PaymentErrorResult parseErrorBody(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, PaymentErrorResult.class);
        } catch (IOException e) {
            throw new PaymentException("에러 메세지를 불러올 수 없습니다.");
        }
    }
}
