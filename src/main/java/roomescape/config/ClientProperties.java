package roomescape.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
public record ClientProperties(
        @NotNull
        @Positive
        long connectionTimeoutSecond,

        @NotNull
        @Positive
        long readTimeoutSecond
) {
}
