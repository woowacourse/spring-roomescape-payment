package roomescape.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
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
        try {
            return restClient.post()
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(generatedConfirmRequestBody(request))
                    .retrieve()
                    .body(PaymentResult.class);
        } catch (RestClientResponseException exception) {
            String responseBody = exception.getResponseBodyAsString();
            throw new PaymentException(parseErrorBody(responseBody));
        }
    }

    private HttpHeaders generateHttpHeaders() {
        String header = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + header);
        return headers;
    }

    private Map<String, Object> generatedConfirmRequestBody(ReservationRequest request) {
        return Map.of(
                "amount", request.amount(),
                "orderId", request.orderId(),
                "paymentKey", request.paymentKey()
        );
    }

    private PaymentErrorResult parseErrorBody(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, PaymentErrorResult.class);
        } catch (IOException e) {
            throw new PaymentException("에러 메세지를 불러올 수 없습니다.");
        }
    }
}
