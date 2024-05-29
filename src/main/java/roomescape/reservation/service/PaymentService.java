package roomescape.reservation.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.dto.PaymentResponse;

@Service
public class PaymentService {

    @Value("${custom.security.toss-payment.secret-key}")
    private String tossSecretKey;

    public PaymentResponse requestTossPayment(PaymentRequest paymentRequest) {
        byte[] encoded = Base64.getEncoder().encode((tossSecretKey + "moaKey:").getBytes(StandardCharsets.UTF_8));
        String authorization = "Basic " + new String(encoded);

        RestClient tossRestClient = RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments").build();
        return tossRestClient.post()
                .uri("/confirm")
                .header("Authorization", authorization)
                .body(paymentRequest, new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(r -> r.is4xxClientError() || r.is5xxServerError(), (request, response) -> {
                    InputStream body = response.getBody();
                    Reader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
                    JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStreamReader);
                    String errorMessage = jsonObject.get("message").toString().replace("\"", "");

                    throw new PaymentException("결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value()));
                })
                .toEntity(PaymentResponse.class)
                .getBody();
    }
}
