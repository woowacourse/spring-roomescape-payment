package roomescape.payment.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("payment.toss")
public class TossClientProperties {

    private final String baseUrl;
    private final String approvalUrl;
    private final String authToken;
}
