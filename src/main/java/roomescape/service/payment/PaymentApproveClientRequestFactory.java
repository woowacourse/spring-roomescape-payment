package roomescape.service.payment;

import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentApproveClientRequestFactory extends SimpleClientHttpRequestFactory {

    public PaymentApproveClientRequestFactory() {
        super.setConnectTimeout(Duration.ofSeconds(2));
        super.setReadTimeout(Duration.ofSeconds(8));
    }
}
