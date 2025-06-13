package roomescape.payment.infrastructure.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payment-api.toss")
@Getter
@Setter
public class TossPaymentProperties {

    private String secretKey;
    private String baseUrl;
    private int connectionTimeout;
    private int readTimeout;
}
