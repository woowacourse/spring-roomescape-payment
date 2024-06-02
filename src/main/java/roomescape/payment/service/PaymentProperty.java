package roomescape.payment.service;

public class PaymentProperty {
    private String name;
    private String url;
    private int connectionTimeout;
    private int readTimeout;
    private String secretKey;

    public String getName() {
        return name;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
