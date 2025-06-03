package roomescape.payment.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final Map<String, Vendor> properties = new HashMap<>();

    @Getter
    @AllArgsConstructor
    public static class Vendor {
        private final String secretKey;
        private final String baseUrl;
        private final int connectTimeout;
        private final int readTimeout;
    }
}


