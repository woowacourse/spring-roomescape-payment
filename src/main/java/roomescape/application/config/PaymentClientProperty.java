package roomescape.application.config;

public record PaymentClientProperty(String name, String url, String secret,
                                    long connectionTimeoutInSeconds, long readTimeoutInSeconds) {
}
