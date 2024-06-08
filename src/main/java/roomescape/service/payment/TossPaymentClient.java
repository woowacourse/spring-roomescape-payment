package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.Payment;
import roomescape.exception.PaymentException;
import roomescape.service.payment.dto.PaymentErrorResult;
import roomescape.service.payment.dto.PaymentRequest;
import roomescape.service.payment.dto.PaymentResult;

public class TossPaymentClient {
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    @Value("${tosspay.secret_key}")
    private String secretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public PaymentResult confirm(PaymentRequest request) {
        HttpHeaders headers = generateHttpHeaders();
        ConfirmRequest requestData = new ConfirmRequest(request.amount(), request.orderId(), request.paymentKey());

        try {
            return restClient.post()
                    .uri("/confirm")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(requestData)
                    .retrieve()
                    .body(PaymentResult.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new PaymentException(parseErrorBody(responseBody), e);
        }
    }

    public void cancel(Payment payment) {
        validatePayment(payment);
        HttpHeaders headers = generateHttpHeaders();
        CancelRequest cancelRequest = new CancelRequest("고객이 취소를 원함");

        try {
            restClient.post()
                    .uri(String.format("/%s/cancel", payment.getPaymentKey()))
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(cancelRequest)
                    .retrieve();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new PaymentException(parseErrorBody(responseBody), e);
        }
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new PaymentException("결제정보가 비어 있습니다.");
        }
    }

    private HttpHeaders generateHttpHeaders() {
        String header = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, BASIC_AUTH_PREFIX + header);
        return headers;
    }

    private PaymentErrorResult parseErrorBody(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, PaymentErrorResult.class);
        } catch (IOException e) {
            throw new PaymentException("에러 메세지를 불러올 수 없습니다.");
        }
    }

    private record ConfirmRequest(BigDecimal amount, String orderId, String paymentKey) {
    }

    private record CancelRequest(String cancelReason) {
    }
}
