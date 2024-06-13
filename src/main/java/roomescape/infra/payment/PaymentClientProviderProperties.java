package roomescape.infra.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;

public record PaymentClientProviderProperties(

        @NotBlank
        String name,

        @NotNull
        Duration connectionTimeoutInSeconds,

        @NotNull
        Duration readTimeoutInSeconds,

        @NotBlank
        @Pattern(regexp = "https://.*")
        String baseUrl,

        @NotBlank
        String secretKey
) {
}
