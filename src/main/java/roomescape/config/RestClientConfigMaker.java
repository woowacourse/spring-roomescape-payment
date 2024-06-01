package roomescape.config;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.paymenthistory.domain.PaymentUrl;

public class RestClientConfigMaker {

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 30000;

    private final PaymentUrl paymentUrl;

    public RestClientConfigMaker(PaymentUrl paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public RestClient makeRestClient() {
        return RestClient.builder()
                .baseUrl(paymentUrl.getPaymentUrl())
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();

        simpleClientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        simpleClientHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
        return simpleClientHttpRequestFactory;
    }
}
