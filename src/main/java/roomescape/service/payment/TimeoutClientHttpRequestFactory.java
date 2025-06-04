package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TimeoutClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    public TimeoutClientHttpRequestFactory(
            @Value("${http-client.connect-timeout-millis}") int connectTimeoutMillis,
            @Value("${http-client.read-timeout-millis}") int readTimeoutMillis
    ) {
        super.setConnectTimeout(Duration.ofMillis(connectTimeoutMillis));
        super.setReadTimeout(Duration.ofMillis(readTimeoutMillis));
    }
}
