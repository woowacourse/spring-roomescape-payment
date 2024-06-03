package roomescape.application.payment.config;

import java.time.Duration;

public record PaymentClientProperty(String name, String url, String secret,
                                    Duration connectionTimeoutInSeconds, Duration readTimeoutInSeconds) {
}
