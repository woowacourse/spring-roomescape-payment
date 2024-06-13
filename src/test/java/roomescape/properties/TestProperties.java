package roomescape.properties;

import roomescape.config.properties.PaymentProperties;
import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.SecretKey;
import roomescape.paymenthistory.domain.TimeOut;

public class TestProperties implements PaymentProperties {

    public TestProperties() {
    }

    @Override
    public SecretKey getSecretKey() {
        return new SecretKey("secretKey");
    }

    @Override
    public PaymentUrl getPaymentUrl() {
        return new PaymentUrl("paymentUrl");
    }

    @Override
    public TimeOut getConnectTimeOut() {
        return new TimeOut("1");
    }

    @Override
    public TimeOut getReadTimeOut() {
        return new TimeOut("1");
    }
}
