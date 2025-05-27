package roomescape.reservation.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
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

    public static final String TOSS_PAYMENTS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final RestClient restClient;
    private final JsonStringToObject jsonStringToObject;
    private final String tossSecretKey;

    public PaymentServiceImpl(
            final RestClient restClient,
            final JsonStringToObject jsonStringToObject,
            @Value("${api.toss.secret-key}") String tossSecretKey
    ) {
        this.restClient = restClient;
        this.jsonStringToObject = jsonStringToObject;
        this.tossSecretKey = tossSecretKey;
    }

    /*
    TODO
    - 요청을 쏴주는 객체 만들기
    - RequestBody만들어 주는 객체 만들기
    - Base64EncodeKey 이런 객체 만들어서 사용하기
        - PaymentKey객체를 만들고, 이거의 변수로 Base64Encode~
    - BasicAuthorization객체 만들기

     */
    @Override
    public PaymentInfo paymentReservation(final PaymentInfo paymentInfo) {
        String encodedAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        Map<String, Object> requestBody = Map.of(
                "paymentKey", paymentInfo.paymentKey(),
                "amount", paymentInfo.totalAmount(),
                "orderId", paymentInfo.orderId()
        );
        try {
            return restClient.post()
                    .uri(TOSS_PAYMENTS_CONFIRM_URL)
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
