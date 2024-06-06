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
import roomescape.reservation.encoder.BasicAuthEncoder;
import roomescape.reservation.model.Payment;
import roomescape.reservation.repository.PaymentRepository;

@Service
public class PaymentService {

    private final RestClient tossRestClient;
    private final PaymentRepository paymentRepository;

    @Value("${custom.security.toss-payment.secret-key}")
    private String tossSecretKey;
    @Value("${third-party-api.toss-payment.path.payment-confirm}")
    private String confirmPath;

    public PaymentService(RestTemplateBuilder builder,
                          @Value("${third-party-api.toss-payment.url}") String url,
                          @Value("${third-party-api.toss-payment.path.payment}") String path,
                          PaymentRepository paymentRepository) {
        RestTemplate tossPaymentRestTemplate = builder.setConnectTimeout(Duration.of(3000, ChronoUnit.MILLIS))
                .setReadTimeout(Duration.of(1000, ChronoUnit.MILLIS))
                .uriTemplateHandler(new DefaultUriBuilderFactory(url + path))
                .build();
        this.tossRestClient = RestClient.create(tossPaymentRestTemplate);
        this.paymentRepository = paymentRepository;
    }

    public Payment requestTossPayment(PaymentRequest paymentRequest) {
        String authorization = BasicAuthEncoder.encode(tossSecretKey);

        Payment payment = tossRestClient.post()
                .uri(confirmPath)
                .header("Authorization", authorization)
                .body(paymentRequest, new ParameterizedTypeReference<>() {
                })
                .retrieve()
                .onStatus(r -> r.is4xxClientError() || r.is5xxServerError(), (request, response) -> {
                    String errorMessage = parseErrorMessage(response);
                    throw new PaymentException("결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value()));
                })
                .toEntity(Payment.class)
                .getBody();

        return paymentRepository.save(payment);
    }

    private String parseErrorMessage(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        Reader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStreamReader);
        return jsonObject.get("message").toString().replace("\"", "");
    }
}
