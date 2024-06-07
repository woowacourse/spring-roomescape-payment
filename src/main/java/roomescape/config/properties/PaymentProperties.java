package roomescape.config.properties;

import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.SecretKey;
import roomescape.paymenthistory.domain.TimeOut;

public interface PaymentProperties {

    SecretKey getSecretKey();

    PaymentUrl getPaymentUrl();

    TimeOut getConnectTimeOut();

    TimeOut getReadTimeOut();
}
