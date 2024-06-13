package roomescape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.payments")
public record TossPaymentsProperties(String baseUrl, Api api, String widgetSecretKey) {

    public record Api(String confirmUrl, String refundUrlTemplate) {
    }
}
