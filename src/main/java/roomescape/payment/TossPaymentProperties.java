package roomescape.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentProperties {

    private final String secretKey;

    private final String baseUrl;

    private final String confirmPath;

    private final String cancelPath;

    private final String inquiryPath;

    public TossPaymentProperties(String secretKey, String baseUrl, String confirmPath, String cancelPath,
                                 String inquiryPath) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
        this.confirmPath = confirmPath;
        this.cancelPath = cancelPath;
        this.inquiryPath = inquiryPath;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getConfirmUrl() {
        return baseUrl + confirmPath;
    }

    public String getCancelUrl() {
        return baseUrl + cancelPath;
    }

    public String getInquiryUrl() {
        return baseUrl + inquiryPath;
    }
}
