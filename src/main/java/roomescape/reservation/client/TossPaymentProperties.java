package roomescape.reservation.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("payment.toss")
public class TossPaymentProperties {

    private final String authToken;
    private final String baseUrl;
}
