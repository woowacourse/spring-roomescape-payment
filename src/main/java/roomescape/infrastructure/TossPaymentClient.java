package roomescape.infrastructure;

import java.math.BigDecimal;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.controller.handler.exception.PaymentException;
import roomescape.domain.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.dto.response.ExceptionInfo;

public class TossPaymentClient implements PaymentClient {

    @Value("${atto.ash.secret-key}")
    private String secretKey;
    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo payment(MemberReservationRequest memberReservationRequest) {
        BigDecimal amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);
        String base64SecretKey = Base64.getEncoder().encodeToString((secretKey).getBytes());

        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", "Basic " + base64SecretKey)
                    .body(paymentInfo)
                    .retrieve()
                    .body(PaymentInfo.class);
        } catch (HttpClientErrorException exception) {
            ExceptionInfo exceptionInfo = exception.getResponseBodyAs(ExceptionInfo.class);
            throw new PaymentException(exceptionInfo);
        }
    }
}
