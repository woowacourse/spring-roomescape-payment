package roomescape.web.controller.api.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(PaymentConfiguration.class)
public class PaymentAuthorizationGenerator {

    private final PaymentConfiguration paymentConfiguration;

    public PaymentAuthorizationGenerator(PaymentConfiguration paymentConfiguration) {
        this.paymentConfiguration = paymentConfiguration;
    }

    public String createAuthorizations() {
        String widgetSecretKey = paymentConfiguration.secretKey();
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
