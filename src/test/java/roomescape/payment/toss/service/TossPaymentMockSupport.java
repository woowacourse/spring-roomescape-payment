package roomescape.payment.toss.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.payment.toss.config.TestTossPaymentConfig;

public abstract class TossPaymentMockSupport {

    protected static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";

    @Autowired
    protected MockRestServiceServer mockServer;

    @Autowired
    protected TestTossPaymentConfig testTossPaymentConfig;

    protected TossPaymentClient tossPaymentClient;

    @PostConstruct
    void setUp() {
        RestClient.Builder builder = testTossPaymentConfig.restClientBuilder();
        mockServer = MockRestServiceServer.bindTo(builder).build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(builder.build()))
                .build();

        tossPaymentClient = factory.createClient(TossPaymentClient.class);
    }
}
