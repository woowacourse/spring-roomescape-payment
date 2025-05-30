package roomescape.payment.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.error.ClientErrorHandler;
import roomescape.payment.error.ServerErrorHandler;

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
                .onStatus(new ClientErrorHandler())
                .onStatus(new ServerErrorHandler())
                .body(PaymentConfirmResponse.class);
    }
}
