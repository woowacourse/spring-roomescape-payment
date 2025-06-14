package roomescape.payment.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.error.exception.ExternalApiClientException;
import roomescape.global.error.exception.ExternalApiServerException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.PaymentErrorResponse;

@Slf4j
@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(@Qualifier("tossPaymentRestClient") RestClient restClient) {
        this.restClient = restClient;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public PaymentConfirmResponse requestPaymentConfirm(String paymentKey, String orderId, Long amount) {
        PaymentConfirmResponse paymentConfirmResponse = restClient.post()
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                                    response.getBody(),
                                    PaymentErrorResponse.class
                            );
                            throw new ExternalApiClientException("잘못된 사용자 결제 요청입니다.\n" + paymentErrorResponse.message());
                        }
                )
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                                    response.getBody(),
                                    PaymentErrorResponse.class
                            );
                            throw new ExternalApiServerException(
                                    "현재 외부 서비스에 문제가 발생하여 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.\n" + paymentErrorResponse.message());
                        }
                )
                .body(PaymentConfirmResponse.class);
        validateAmount(amount, paymentConfirmResponse.totalAmount());
        return paymentConfirmResponse;
    }

    private void validateAmount(Long requestedAmount, Long actualAmount) {
        if (!requestedAmount.equals(actualAmount)) {
            throw new ExternalApiClientException("결제 요청 금액과 승인 금액이 일치하지 않습니다.");
        }
    }
}
