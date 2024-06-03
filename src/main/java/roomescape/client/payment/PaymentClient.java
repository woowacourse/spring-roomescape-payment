package roomescape.client.payment;

import static roomescape.exception.model.PaymentConfirmExceptionCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.client.payment.dto.PaymentConfirmToTossDto;
import roomescape.exception.ExceptionResponse;
import roomescape.exception.PaymentConfirmException;
import roomescape.exception.global.GlobalExceptionCode;
import roomescape.util.LoggerUtil;

public class PaymentClient {

    private static final Logger log = LoggerUtil.getLogger(PaymentClient.class);

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(String widgetSecretKey, RestClient restClient, ObjectMapper objectMapper) {
        this.encodedSecretKey = Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes());
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public void sendPaymentConfirmToToss(PaymentConfirmToTossDto paymentConfirmToTossDto) {
        String authorizations = "Basic " + encodedSecretKey;

        try {
            restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentConfirmToTossDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        throwConvertedCustomException(response)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new PaymentConfirmException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
                    })
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.error("토스 결제 응답 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getResponseBodyAsString(),
                    e.getCause());
            throw new PaymentConfirmException(GlobalExceptionCode.HTTP_RESPONSE_DATA_INVALID);
        } catch (Exception e) {
            log.error("토스 결제 에러 message: {}, body: {}, cause: {}", e.getMessage(), e.getCause(),
                    e.getCause());
            throw new PaymentConfirmException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void throwConvertedCustomException(ClientHttpResponse response) throws IOException {
        try {
            ExceptionResponse paymentException = objectMapper.readValue(response.getBody(), ExceptionResponse.class);
            throw new PaymentConfirmException(
                    paymentException.code() + paymentException.message(),
                    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING
            );
        } catch (JsonProcessingException e) {
            throw new PaymentConfirmException(GlobalExceptionCode.JSON_DATA_INVALID);
        }
    }

    public String getEncodedSecretKey() {
        return encodedSecretKey;
    }
}

