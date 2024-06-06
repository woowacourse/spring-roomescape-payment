package roomescape.infrastructure.payment;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payments")
public class PaymentProperties {
    private final Map<String, PaymentProperty> clients;

    public PaymentProperties(Map<String, PaymentProperty> clients) {
        this.clients = clients;
    }

    public PaymentProperty getProperty(String clientName) {
        return clients.get(clientName);
    }

    public static class PaymentProperty {
        private String secretKey;
        private String password;
        private int connectionTimeoutSeconds;
        private int readTimeoutSeconds;

        public String secretKey() {
            return secretKey;
        }

        public String password() {
            return password;
        }

        public int connectionTimeoutSeconds() {
            return connectionTimeoutSeconds;
        }

        public int readTimeoutSeconds() {
            return readTimeoutSeconds;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
            this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        }

        public void setReadTimeoutSeconds(int readTimeoutSeconds) {
            this.readTimeoutSeconds = readTimeoutSeconds;
        }
    }
}
