package roomescape.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import roomescape.common.exception.PaymentException;
import roomescape.reservation.dto.request.PaymentConfirmRequest;
import roomescape.reservation.dto.response.PaymentResponse;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public PaymentService(
            @Value("${payment.secret-key}") String secretKey,
            ObjectMapper objectMapper
    ) {
        this.restClient = RestClient.create();
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .build()
                .post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(this::getIsError, ((request, response) -> {
                    PaymentResponse paymentResponse = objectMapper.readValue(response.getBody(), PaymentResponse.class);
                    throw new PaymentException(response.getStatusCode(), paymentResponse.message());
                }));

//        try {
//            return body.retrieve().body(PaymentResponse.class);
//
//        } catch (HttpClientErrorException e) {
//            body.exchange((request, response) -> {
//                if (response.getStatusCode().isError()) {
//                    throw new PaymentException(response.getStatusCode(), e.getMessage());
//                }
//                return null;
//            });
//        }
//        return null;
    }

    private boolean getIsError(HttpStatusCode statusCode) {
        System.out.println(" statusCode : " + statusCode);
        return statusCode.isError();
    }
}
