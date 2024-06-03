package roomescape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payment.toss")
public record PaymentProperties(String secretKey, PaymentUrl url, PaymentTimeOut timeOut) {
    public record PaymentUrl(String base, String paymentConfirm) {
    }

    public record PaymentTimeOut(Long connection, Long read) {
    }
}
