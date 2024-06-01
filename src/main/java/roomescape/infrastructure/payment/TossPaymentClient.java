package roomescape.infrastructure.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.PaymentClient;
import roomescape.service.request.PaymentApproveAppRequest;
import roomescape.service.response.PaymentApproveSuccessAppResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPaymentClient.class);
    private static final String CONFIRM_URL = "/confirm";

    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;

    public TossPaymentClient(PaymentAuthorizationGenerator paymentAuthorizationGenerator,
                             RestClient.Builder restClient,
                             @Value("${payment.base-url}") String baseUrl) {
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = restClient.baseUrl(baseUrl).build();
    }

    public PaymentApproveSuccessAppResponse approve(PaymentApproveAppRequest paymentApproveAppRequest) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        return restClient.post()
                .uri(CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentApproveAppRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, handleClientError())
                .onStatus(HttpStatusCode::is5xxServerError, handleServerError())
                .body(PaymentApproveSuccessAppResponse.class);
    }

    private ErrorHandler handleClientError() {
        return (request, response) -> {
            LOGGER.error("토스 결제 에러 message: {}, body: {}",
                    response.getStatusCode(),
                    response.getStatusText());
            throw new RoomescapeException(RoomescapeErrorCode.PAYMENT_FAILED,
                    String.format("결제 승인 요청 처리 중 예외가 발생했습니다."));
        };
    }

    private ErrorHandler handleServerError() {
        return (request, response) -> {
            LOGGER.error("TossPaymentClient 서버 에러 message: {}, body: {}",
                    response.getStatusCode(),
                    response.getStatusText());
            throw new RoomescapeException(RoomescapeErrorCode.INTERNAL_SERVER_ERROR,
                    String.format("결제 승인 처리 API 서버에서 에러가 발생했습니다."));
        };
    }
}
