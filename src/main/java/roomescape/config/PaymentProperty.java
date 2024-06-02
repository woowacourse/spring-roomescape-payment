package roomescape.config;

public record PaymentProperty(String vendor, String baseUrl, String secretKey, long connectionTimeoutSeconds, long readTimeoutSeconds) {
}
