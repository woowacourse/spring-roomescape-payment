package roomescape.config;

import java.time.Duration;

public class TossPaymentProperties {

    private String url;
    private String path;
    private String confirmPath;
    private Duration connectTimeout;
    private Duration readTimeout;
    private String secretKey;

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getConfirmPath() {
        return confirmPath;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setConfirmPath(String confirmPath) {
        this.confirmPath = confirmPath;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
