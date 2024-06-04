package roomescape.infrastructure.payment;

public record PaymentProperty(String vendor,
                              long connectionTimeoutSeconds,
                              long readTimeoutSeconds) {
}
