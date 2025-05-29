package roomescape.service.payment;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

@Component
public class MyClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

    public MyClientHttpRequestFactory() {
        super.setConnectTimeout(1);
        super.setReadTimeout(1);
    }
}
