package roomescape.client.payment;

import static roomescape.exception.model.PaymentConfirmExceptionCode.FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.client.payment.dto.TossPaymentConfirmRequest;
import roomescape.client.payment.dto.TossPaymentConfirmResponse;
import roomescape.exception.PaymentConfirmException;
import roomescape.exception.TossPaymentExceptionResponse;
import roomescape.exception.global.GlobalExceptionCode;
import roomescape.util.LoggerUtil;

@Tag(name = "외부 api에 결제를 요청하는 Client", description = "토스 결제 api에 결제 승인을 요청하고 승인시 결제 정보와 관련된 응답을 받고, 미 승인시 에러 객체를 반환한다.")
public class PaymentClient {

    private static final Logger log = LoggerUtil.getLogger(PaymentClient.class);

    private final String encodedSecretKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PaymentClient(String widgetSecretKey, RestClient restClient, ObjectMapper objectMapper) {
        this.encodedSecretKey = Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes());
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public TossPaymentConfirmResponse sendPaymentConfirmToToss(TossPaymentConfirmRequest tossPaymentConfirmRequest) {
        String authorizations = "Basic " + encodedSecretKey;

        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", authorizations)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(tossPaymentConfirmRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                            throwConvertedCustomException(response)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new PaymentConfirmException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
                    })
                    .body(TossPaymentConfirmResponse.class);
        } catch (RestClientResponseException e) {
            log.error("[토스 결제 api 실패] message: {}, body: {}", e.getMessage(), e.getResponseBodyAsString(),
                    e.getCause());
            throw new PaymentConfirmException(GlobalExceptionCode.HTTP_RESPONSE_DATA_INVALID);
        } catch (Exception e) {
            log.error("[토스 결제 실패] message: {}", e.getMessage(), e.getCause());
            throw new PaymentConfirmException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void throwConvertedCustomException(ClientHttpResponse response) {
        try {
            ByteArrayInputStream responseBody = convertResponseBody(response);
            TossPaymentExceptionResponse paymentException =
                    objectMapper.readValue(responseBody, TossPaymentExceptionResponse.class);
            throw new PaymentConfirmException(
                    paymentException.code() + paymentException.message(),
                    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING
            );
        } catch (JsonProcessingException e) {
            throw new PaymentConfirmException(GlobalExceptionCode.JSON_DATA_INVALID);
        } catch (IOException e) {
            throw new PaymentConfirmException(GlobalExceptionCode.HTTP_RESPONSE_DATA_INVALID);
        }
    }

    private ByteArrayInputStream convertResponseBody(ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.error("[토스 api 예외 응답 객체] body: {}", responseBody);

        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(responseBodyBytes);
    }

    public String getEncodedSecretKey() {
        return encodedSecretKey;
    }
}

