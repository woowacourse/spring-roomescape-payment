package roomescape.maker;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.config.properties.PaymentProperties;
import roomescape.paymenthistory.domain.PaymentUrl;
import roomescape.paymenthistory.domain.TimeOut;

public class RestClientConfigMaker {

    private final PaymentUrl paymentUrl;
    private final TimeOut connectTimeOut;
    private final TimeOut readTimeOut;

    public RestClientConfigMaker(PaymentProperties properties) {
        this.paymentUrl = properties.getPaymentUrl();
        this.connectTimeOut = properties.getConnectTimeOut();
        this.readTimeOut = properties.getReadTimeOut();
    }

    public RestClient makeRestClient() {
        return RestClient.builder()
                .baseUrl(paymentUrl.getPaymentUrl())
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();

        simpleClientHttpRequestFactory.setConnectTimeout(connectTimeOut.getTimeOutTime());
        simpleClientHttpRequestFactory.setReadTimeout(readTimeOut.getTimeOutTime());
        return simpleClientHttpRequestFactory;
    }
}
