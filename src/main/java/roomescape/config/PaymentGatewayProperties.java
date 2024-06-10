package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "payment")
public class PaymentGatewayProperties {
    private final Map<String, Gateway> gateways;

    public PaymentGatewayProperties(Map<String, Gateway> gateways) {
        this.gateways = gateways;
    }

    public Gateway getGateway(String gatewayName) {
        return gateways.get(gatewayName);
    }

    public static class Gateway {
        private final String uri;
        private final String secretKey;
        private final Duration connectTimeout;
        private final Duration readTimeout;

        @ConstructorBinding
        private Gateway(String uri, String secretKey, Duration connectTimeout, Duration readTimeout) {
            this.uri = uri;
            this.secretKey = secretKey;
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
        }

        public String getUri() {
            return uri;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public Duration getReadTimeout() {
            return readTimeout;
        }
    }
}
