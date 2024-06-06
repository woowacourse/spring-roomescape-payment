package roomescape.config;

public record PaymentProperty(String name, String secretKey, String baseUrl, long connectionTime, long readTime) {
}
