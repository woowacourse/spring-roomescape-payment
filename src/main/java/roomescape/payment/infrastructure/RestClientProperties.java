package roomescape.payment.infrastructure;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("rest-client.toss-payment")
@Getter
@Validated
@RequiredArgsConstructor
public class RestClientProperties {

   // @NotBlank(message = "baseUrl은 비어있을 수 없습니다.")
    private final String baseUrl;
}
