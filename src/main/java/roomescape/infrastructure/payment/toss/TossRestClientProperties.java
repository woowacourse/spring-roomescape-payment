package roomescape.infrastructure.payment.toss;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import roomescape.infrastructure.payment.PaymentClientProperties;

@Component
@ConfigurationProperties(prefix = "payment.toss")
@Setter
public class TossRestClientProperties implements PaymentClientProperties {

    private int connectTimeout;
    private int readTimeout;
    private String baseUrl;

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
