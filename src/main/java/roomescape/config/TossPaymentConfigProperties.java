package roomescape.config;

public class TossPaymentConfigProperties {

    private String secret;
    private String baseUri;
    private String confirmUri;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getConfirmUri() {
        return confirmUri;
    }

    public void setConfirmUri(String confirmUri) {
        this.confirmUri = confirmUri;
    }
}
