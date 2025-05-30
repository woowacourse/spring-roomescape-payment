package roomescape.study;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.dto.business.PaymentResult;
import roomescape.utility.PaymentClient;
import roomescape.utility.TossPaymentClient;

@Disabled
public class PaymentApiTest {

    private String orderId;
    private String paymentKey;
    private long amount;
    private PaymentClient realPaymentClient;

    public PaymentApiTest(
            @Value("${toss_payment_base_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey,
            @Value("${toss_order_id}") String orderId,
            @Value("${toss_order_payment_key}") String paymentKey,
            @Value("${toss_order_amount}") long amount,
            @Value("${toss_confirm_server_uri}") String confirmServerUri
    ) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        RestClient realRestClient = RestClient.builder().baseUrl(paymentUrl).build();
        this.realPaymentClient = new TossPaymentClient(realRestClient, secretKey, confirmServerUri);
    }


    @Test
    void whenPaymentIsCorrectThenSuccess() {
        assertThat(realPaymentClient.pay(orderId, paymentKey, amount))
                .isEqualTo(new PaymentResult(orderId, paymentKey, amount));

    }
}
