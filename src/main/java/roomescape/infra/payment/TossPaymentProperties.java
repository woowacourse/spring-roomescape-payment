package roomescape.infra.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments")
public record TossPaymentProperties(
        String url,
        String secretKey,
        int readTimeout,
        int connectTimeout
) {

    @Override
    public String secretKey() {
        return secretKey + ":";
    }
}
