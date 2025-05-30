package roomescape.payment.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private String tossPaymentBaseUrl;

    private int tossPaymentTimeoutSeconds;

    private String tossSecretKey;
}
