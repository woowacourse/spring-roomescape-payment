package roomescape.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(String secretKey, Api api, Timeout timeout) {

    public record Api(String base, String confirm, String cancel) {
    }

    public record Timeout(int connection, int read) {
    }
}
