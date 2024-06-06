package roomescape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(String secretKey, Url url, TimeOut timeOut) {

    public record Url(String base, String confirm) {
    }

    public record TimeOut(Long connection, Long read) {
    }
}

