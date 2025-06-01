package roomescape.payment.toss.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "payment.toss")
public class TossPaymentConfigProperties {

    private String url;
    @NotBlank
    private String token;
    private int connectionTimeoutMs;

}
