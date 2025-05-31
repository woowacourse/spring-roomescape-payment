package roomescape.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final ResponseErrorHandler paymentClientErrorHandler;
    private final ResponseErrorHandler paymentServerErrorHandler;

    public TossPaymentClient(
            @Qualifier("tossPaymentRestClient") RestClient restClient,
            @Qualifier("paymentClientErrorHandler") ResponseErrorHandler clientErrorHandler,
            @Qualifier("paymentServerErrorHandler") ResponseErrorHandler serverErrorHandler
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
                .onStatus(paymentClientErrorHandler)
                .onStatus(paymentServerErrorHandler)
                .body(PaymentConfirmResponse.class);
    }
}
