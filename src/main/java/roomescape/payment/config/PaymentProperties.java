package roomescape.payment.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import roomescape.payment.dto.PaymentProviderInfo;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final Map<String, PaymentProviderInfo> properties = new HashMap<>();

}


