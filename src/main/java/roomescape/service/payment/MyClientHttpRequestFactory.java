package roomescape.service.payment;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MyClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    public MyClientHttpRequestFactory() {
        super.setConnectTimeout(Duration.ofSeconds(1));
        super.setReadTimeout(Duration.ofSeconds(1));
    }
}
