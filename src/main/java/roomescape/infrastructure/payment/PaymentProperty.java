package roomescape.infrastructure.payment;

public record PaymentProperty(String vendor,
                              String url,
                              String secretKey,
                              long connectionTimeoutSeconds,
                              long readTimeoutSeconds) {
}
