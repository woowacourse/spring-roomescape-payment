package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.converter.JsonStringToObject;
import roomescape.global.dto.ExternalApiErrorResponse;
import roomescape.global.exception.ExternalApiException;
import roomescape.reservation.domain.PaymentInfo;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final RestClient restClient;
    private final JsonStringToObject jsonStringToObject;

    public PaymentServiceImpl(final RestClient restClient, final JsonStringToObject jsonStringToObject) {
        this.restClient = restClient;
        this.jsonStringToObject = jsonStringToObject;
    }

    @Override
    public PaymentInfo paymentReservation(final PaymentInfo paymentInfo) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentInfo.paymentKey(),
                "amount", paymentInfo.totalAmount(),
                "orderId", paymentInfo.orderId() + "Dsadsadsa"
        );
        try {
            return restClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(PaymentInfo.class);
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new ExternalApiException(new ExternalApiErrorResponse(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    jsonStringToObject.convertTossApiErrorResponse(responseBody).message()
            ));
        }
    }
}
