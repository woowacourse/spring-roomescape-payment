package roomescape.config.payment;

public record PaymentProperty(String vendor, String baseUrl, String secretKey, long connectionTimeoutSeconds, long readTimeoutSeconds) {
}
