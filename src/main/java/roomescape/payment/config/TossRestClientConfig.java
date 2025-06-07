package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.auth.util.AuthUtil;
import roomescape.payment.exception.TossPaymentErrorHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossRestClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    public TossRestClientConfig(TossPaymentProperties tossPaymentProperties) {
        this.tossPaymentProperties = tossPaymentProperties;
    }

    @Bean(name = "tossRestClient")
    public RestClient tossRestClient() {
        return createRestClient(new TossPaymentErrorHandler());
    }

    private RestClient createRestClient(RestClient.ResponseSpec.ErrorHandler errorHandler) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(tossPaymentProperties.connectTimeout());
        factory.setReadTimeout(tossPaymentProperties.readTimeout());

        RestClient.Builder builder = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(tossPaymentProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, AuthUtil.encodeBasicAuth(tossPaymentProperties.secretKey()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);

        if (errorHandler != null) {
            builder.defaultStatusHandler(HttpStatusCode::isError, errorHandler);
        }

        return builder.build();
    }
}
