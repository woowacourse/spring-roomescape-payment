package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.exception.BadRequestException;
import roomescape.payment.dto.PaymentFailure;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

import java.util.Base64;

@Service
public class PaymentService {

    private final ObjectMapper objectMapper;

    private final RestClient restClient;
    @Value("${toss.secret}")
    private String tossSecretKey;

    public PaymentService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public void confirmPayment(PaymentRequest request) {
        String token = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());

        PaymentResponse response = restClient.post()
                .uri("/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    PaymentFailure paymentFailure = objectMapper.readValue(res.getBody(), PaymentFailure.class);
                    throw new BadRequestException(paymentFailure.message());
                })
                .body(PaymentResponse.class);
    }
}
