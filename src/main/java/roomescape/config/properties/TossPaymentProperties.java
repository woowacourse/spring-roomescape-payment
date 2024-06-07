package roomescape.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.SecretKey;
import roomescape.paymenthistory.domain.TimeOut;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties implements PaymentProperties {

    private final SecretKey secretKey;
    private final PaymentUrl paymentUrl;
    private final TimeOut connectTimeOut;
    private final TimeOut readTimeOut;

    @ConstructorBinding
    public TossPaymentProperties(String secretKey, String paymentUrl, String connectTimeOut, String readTimeOut) {
        this.secretKey = new SecretKey(secretKey);
        this.paymentUrl = new PaymentUrl(paymentUrl);
        this.connectTimeOut = new TimeOut(connectTimeOut);
        this.readTimeOut = new TimeOut(readTimeOut);
    }

    @Override
    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public PaymentUrl getPaymentUrl() {
        return paymentUrl;
    }

    @Override
    public TimeOut getConnectTimeOut() {
        return connectTimeOut;
    }

    @Override
    public TimeOut getReadTimeOut() {
        return readTimeOut;
    }
}
