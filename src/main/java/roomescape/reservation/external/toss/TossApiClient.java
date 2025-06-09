package roomescape.reservation.external.toss;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.exception.ErrorCode;
import roomescape.global.exception.ExternalApiException;

@Component
@Slf4j
public class TossApiClient {

    private final ObjectMapper objectMapper;
    private final RestClient tossRestClient;

    public TossApiClient(final ObjectMapper objectMapper,
                         final @Qualifier("tossRestClient") RestClient tossRestClient) {
        this.objectMapper = objectMapper;
        this.tossRestClient = tossRestClient;
    }

    public TossPaymentResponse requestPayment(TossPaymentRequest tossPaymentRequest) {
        try {
            return tossRestClient.post()
                    .uri("/payments/confirm")
                    .body(tossPaymentRequest)
                    .retrieve()
                    .body(TossPaymentResponse.class);
        }
        catch (RestClientResponseException e) {
            log.warn("[TOSS-ERROR] 외부 API 응답 오류 - 상태코드: {}, 응답내용: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);

            TossErrorResponse tossErrorResponse = parseErrorResponse(e.getResponseBodyAsString());

            throw new ExternalApiException(new ErrorCode(
                            HttpStatus.valueOf(e.getStatusCode().value()),
                            tossErrorResponse.code(),
                            tossErrorResponse.message()));
        }
    }

    private TossErrorResponse parseErrorResponse(final String errorMessage) {
        try {
            return objectMapper.readValue(errorMessage, TossErrorResponse.class);
        }
        catch (JsonProcessingException e) {
            throw new ExternalApiException(new ErrorCode(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "INVALID_EXTERNAL_API_FORMAT",
                    "외부 API 오류 응답의 형식이 올바르지 않습니다."));
        }
    }
}
