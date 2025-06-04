package roomescape.payment.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.global.error.exception.BadRequestException;
import roomescape.global.error.exception.ServerException;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.PaymentErrorResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(@Qualifier("tossPaymentRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    // TODO: 테스트 고민..
    // restClient를 모킹해야 하는가?
    @Override
    public PaymentConfirmResponse requestPaymentConfirm(String paymentKey, String orderId, Long amount) {
        return restClient.post()
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            ObjectMapper objectMapper = new ObjectMapper()
                                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                                    response.getBody(),
                                    PaymentErrorResponse.class
                            );
                            throw new BadRequestException(paymentErrorResponse.message());
                        }
                )
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            ObjectMapper objectMapper = new ObjectMapper()
                                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                                    response.getBody(),
                                    PaymentErrorResponse.class
                            );
                            throw new ServerException(paymentErrorResponse.message());
                        }
                )
                .body(PaymentConfirmResponse.class);
    }
}
