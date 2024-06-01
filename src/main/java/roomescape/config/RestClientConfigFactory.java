package roomescape.config;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public class RestClientConfigFactory {

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 30000;

    public static RestClient restClient(String paymentUrl) {
        return RestClient.builder()
                .baseUrl(paymentUrl)
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private static SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        simpleClientHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
        return simpleClientHttpRequestFactory;
    }
}
