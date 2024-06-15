package roomescape.config.properties;

public record PaymentClientProperty(String name, String secretKey, String baseUrl,
                                    int connectionTimeoutSeconds, int readTimeoutSeconds) {
}
