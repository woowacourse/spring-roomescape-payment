package roomescape.payment.infrastructure;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties("rest-client.toss-payment")
public class RestClientProperties {

    @NotBlank(message = "baseUrl은 비어있을 수 없습니다.")
    private final String baseUrl;

    @NotNull(message = "connectTimeout은 비어있을 수 없습니다.")
    private final int connectTimeout;

    @NotNull(message = "readTimeout은 비어있을 수 없습니다.")
    private final int readTimeout;

    @NotNull(message = "connectionRequestTimeout 비어있을 수 없습니다.")
    private final int connectionRequestTimeout;
}
