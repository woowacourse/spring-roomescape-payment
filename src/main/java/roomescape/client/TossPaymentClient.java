package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossErrorResponse;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InternalServerException;
import roomescape.common.exception.PaymentException;

@Component
public class TossPaymentClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);
    private final RestClient tossRestClient;
    private final ObjectMapper objectMapper;

    private static final List<String> IGNORABLE_CODE = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH",
            "UNAUTHORIZED_KEY"
    );

    public TossPaymentClient(RestClient tossRestClient, ObjectMapper objectMapper) {
        this.tossRestClient = tossRestClient;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<TossPaymentResponse> confirmPayment(TossPaymentConfirmRequest request) {
        log.info("Toss 결제 확인 요청 시작 - paymentKey: {}, orderId: {}, amount: {}", request.paymentKey(), request.orderId(), request.amount());

        try {
            return tossRestClient.post()
                    .uri("/payments/confirm")
                    .body(request)
                    .retrieve()
                    .toEntity(TossPaymentResponse.class);
        } catch (HttpStatusCodeException e) {
            String rawResponse = e.getResponseBodyAsString();
            log.error("Toss 결제 확인 실패 - HTTP 상태: {}, 응답 바디: {}", e.getStatusCode(), rawResponse);

            try {
                TossPaymentResponse errorBody = objectMapper.readValue(e.getResponseBodyAsString(), TossPaymentResponse.class);
                return ResponseEntity.status(e.getStatusCode()).body(errorBody);
            } catch (Exception parseError) {
                log.error("Toss 에러 응답 파싱 실패: {}", parseError.getMessage());
                throw new InternalServerException();
            }
        }
    }


    public void handleTosPaymentException(ResponseEntity<TossPaymentResponse> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return;
        }

        TossErrorResponse failure = response.getBody().failure();

        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            log.error("Toss 결제 실패 처리 - 상태 코드: {}, 실패 코드: {}, 메시지: {}", response.getStatusCode(), failure.code(), failure.message());
            if (IGNORABLE_CODE.contains(failure.code())) {
                throw new InternalServerException();
            }

            throw new PaymentException(response.getStatusCode(), "결제 실패 : " + failure.message());
        }
        log.error("알 수 없는 Toss 결제 오류 발생 - 상태 코드: {}, 실패 코드: {}, 메시지: {}", response.getStatusCode(), failure != null ? failure.code() : "null", failure != null ? failure.message() : "null");
        throw new InternalServerException();
    }
}
