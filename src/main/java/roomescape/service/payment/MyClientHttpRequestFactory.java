package roomescape.service.payment;

import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class MyClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    public MyClientHttpRequestFactory() {
        super.setConnectTimeout(Duration.ofSeconds(5));
        super.setReadTimeout(Duration.ofSeconds(10));
    }
}
