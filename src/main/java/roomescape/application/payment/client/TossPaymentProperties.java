package roomescape.application.payment.client;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "payment.toss")
@Validated
public record TossPaymentProperties(
        @NotNull(message = "payment.toss.base-url는 필수입니다.")
        String baseUrl,
        @NotNull(message = "payment.toss.auth-scheme는 필수입니다.")
        String authScheme,
        @NotNull(message = "payment.toss.secret-key는 필수입니다.")
        String secretKey,
        @NotNull(message = "payment.toss.confirm-path는 필수입니다.")
        String confirmPath
) {

    @Override
    public String secretKey() {
        return secretKey + ":";
    }

    public String confirmUri() {
        return baseUrl + confirmPath;
    }
}
