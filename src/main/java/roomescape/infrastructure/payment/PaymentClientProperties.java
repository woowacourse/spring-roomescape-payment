package roomescape.infrastructure.payment;

public interface PaymentClientProperties {
    String getBaseUrl();

    int getConnectTimeout();

    int getReadTimeout();
}
