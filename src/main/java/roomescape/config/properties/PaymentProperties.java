package roomescape.config.properties;

import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.SecretKey;

public interface PaymentProperties {

    SecretKey getSecretKey();

    PaymentUrl getPaymentUrl();
}
