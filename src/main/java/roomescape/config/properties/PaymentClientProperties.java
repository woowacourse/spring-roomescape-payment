package roomescape.config.properties;

public interface PaymentClientProperties {
    String getSecretKey();
    String getBaseUrl();
    int getConnectionTimeoutSeconds();
    int getReadTimeoutSeconds();
}
