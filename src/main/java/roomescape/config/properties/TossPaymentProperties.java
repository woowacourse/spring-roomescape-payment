package roomescape.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.SecretKey;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties implements PaymentProperties {

    private final SecretKey secretKey;
    private final PaymentUrl paymentUrl;

    @ConstructorBinding
    public TossPaymentProperties(String secretKey, String paymentUrl) {
        this.secretKey = new SecretKey(secretKey);
        this.paymentUrl = new PaymentUrl(paymentUrl);
    }

    @Override
    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public PaymentUrl getPaymentUrl() {
        return paymentUrl;
    }
}
