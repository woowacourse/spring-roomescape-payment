package roomescape.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final ErrorHandler paymentClientErrorHandler;
    private final ErrorHandler paymentServerErrorHandler;

    public TossPaymentClient(
            @Qualifier("tossPaymentRestClient") RestClient restClient,
            @Qualifier("paymentClientErrorHandler") ErrorHandler clientErrorHandler,
            @Qualifier("paymentServerErrorHandler") ErrorHandler serverErrorHandler
    ) {
        this.restClient = restClient;
        this.paymentClientErrorHandler = clientErrorHandler;
        this.paymentServerErrorHandler = serverErrorHandler;
    }

    // TODO: 테스트 고민..
    // restClient를 모킹해야 하는가?
    @Override
    public PaymentConfirmResponse requestPaymentConfirm(String paymentKey, String orderId, Long amount) {
        return restClient.post()
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, paymentClientErrorHandler)
                .onStatus(HttpStatusCode::is5xxServerError, paymentServerErrorHandler)
                .body(PaymentConfirmResponse.class);
    }
}
