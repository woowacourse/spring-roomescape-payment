package roomescape.reservation.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.dto.PaymentResponse;
import roomescape.reservation.encoder.TossSecretKeyEncoder;

@Service
public class PaymentService {

    private final RestClient tossRestClient;

    @Value("${custom.security.toss-payment.secret-key}")
    private String tossSecretKey;

    public PaymentService(RestTemplateBuilder builder) {
        RestTemplate tossPaymentRestTemplate = builder.setConnectTimeout(Duration.of(3000, ChronoUnit.MILLIS))
                .setReadTimeout(Duration.of(1000, ChronoUnit.MILLIS))
                .uriTemplateHandler(new DefaultUriBuilderFactory("https://api.tosspayments.com/v1/payments"))
                .build();
        tossRestClient = RestClient.create(tossPaymentRestTemplate);
    }

    public PaymentResponse requestTossPayment(PaymentRequest paymentRequest) {
        String authorization = TossSecretKeyEncoder.encode(tossSecretKey);

        return tossRestClient.post()
                .uri("/confirm")
                .header("Authorization", authorization)
                .body(paymentRequest, new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(r -> r.is4xxClientError() || r.is5xxServerError(), (request, response) -> {
                    String errorMessage = parseErrorMessage(response);
                    throw new PaymentException("결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value()));
                })
                .toEntity(PaymentResponse.class)
                .getBody();
    }

    private String parseErrorMessage(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        Reader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStreamReader);
        return jsonObject.get("message").toString().replace("\"", "");
    }
}
