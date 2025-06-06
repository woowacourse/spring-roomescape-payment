package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.auth.util.AuthUtil;
import roomescape.payment.dto.PaymentProviderInfo;
import roomescape.payment.exception.TossPaymentErrorHandler;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class RestClientConfig {

    private static final String TOSS_VENDOR = "toss";

    private final PaymentProperties paymentProperties;

    public RestClientConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean(name = "tossRestClient")
    public RestClient tossRestClient() {
        PaymentProviderInfo paymentProviderInfo = findVendor(TOSS_VENDOR);
        return createRestClient(paymentProviderInfo, new TossPaymentErrorHandler());
    }

    private RestClient createRestClient(PaymentProviderInfo paymentProviderInfo, RestClient.ResponseSpec.ErrorHandler errorHandler) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(paymentProviderInfo.connectTimeout());
        factory.setReadTimeout(paymentProviderInfo.readTimeout());

        RestClient.Builder builder = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(paymentProviderInfo.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, AuthUtil.encodeBasicAuth(paymentProviderInfo.secretKey()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        if (errorHandler != null) {
            builder.defaultStatusHandler(HttpStatusCode::isError, errorHandler);
        }

        return builder.build();
    }

    private PaymentProviderInfo findVendor(String vendorName) {
        return Optional.ofNullable(paymentProperties.getProperties().get(vendorName))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 결제 벤더: " + vendorName));
    }
}
