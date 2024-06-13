package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class PaymentAuthorizationProvider {

    private final TossPaymentsProperties tossPaymentsProperties;

    public PaymentAuthorizationProvider(final TossPaymentsProperties tossPaymentsProperties) {
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    public String getAuthorization() {
        final Base64.Encoder encoder = Base64.getEncoder();
        final byte[] encodedBytes = encoder.encode((tossPaymentsProperties.widgetSecretKey() + ":")
                .getBytes(StandardCharsets.UTF_8));

        return "Basic " + new String(encodedBytes);
    }
}
