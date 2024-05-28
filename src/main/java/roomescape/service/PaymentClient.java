package roomescape.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentDto;
import roomescape.exception.PaymentException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public void confirmPayment(PaymentDto paymentDto) {

        final String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        final Base64.Encoder encoder = Base64.getEncoder();
        final byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        final String authorizations = "Basic " + new String(encodedBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            restClient.post()
                    .uri("/confirm")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(paymentDto);
        } catch (HttpClientErrorException e) {
            // TODO: 예외처리 추가
            throw new PaymentException("");
        }
    }
}
