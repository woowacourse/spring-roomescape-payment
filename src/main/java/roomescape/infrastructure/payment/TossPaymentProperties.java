package roomescape.infrastructure.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private final String baseUrl;
    private final String secretKey;
    private final Timeout timeout;

    @Getter
    @RequiredArgsConstructor
    public static class Timeout {
        private final int connect;
        private final int read;
    }
}