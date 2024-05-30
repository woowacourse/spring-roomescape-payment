package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentProperties {

    @Value("${security.payment.toss.secret-key}")
    private String secretKey;

    @Value("${security.payment.toss.host-name}")
    private String hostName;

    public String getSecretKey() {
        return secretKey;
    }

    public String getHostName() {
        return hostName;
    }
}
