package roomescape.infrastructure.payment.config;

public record PaymentProperty(String vendor,
                              String url,
                              String secretKey,
                              long connectionTimeoutSeconds,
                              long readTimeoutSeconds) {
}
