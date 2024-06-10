package roomescape.infra.payment.toss;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.toss")
public record TossPaymentProperties(
        @NotBlank
        @Pattern(regexp = "https://.*")
        String baseUrl,

        @NotBlank
        String secretKey
) {
}
