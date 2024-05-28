package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TossPaymentProperties {

    @Value("${security.payment.toss.secret-key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
