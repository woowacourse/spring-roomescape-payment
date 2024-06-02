package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.TossPaymentErrorHandler;
import roomescape.interceptor.LoggingInterceptor;
import roomescape.service.httpclient.TossPaymentClient;
import roomescape.service.httpclient.TossPaymentRestTemplate;

import java.time.Duration;

@Configuration
public class TossPaymentConfiguration {
    private final TossPaymentErrorHandler tossPaymentErrorHandler;
    private final LoggingInterceptor loggingInterceptor;

    @Value("${toss.secret-key}")
    private String authKey;

    @Value("${toss.base-url}")
    private String baseUrl;

    @Value("${toss.read-timeout}")
    private int readTimeout;

    @Value("${toss.connect-timeout}")
    private int connectTimeout;

    public TossPaymentConfiguration(final TossPaymentErrorHandler tossPaymentErrorHandler, final LoggingInterceptor loggingInterceptor) {
        this.tossPaymentErrorHandler = tossPaymentErrorHandler;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Bean
    TossPaymentClient tossPaymentClient() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, authKey)
                .errorHandler(tossPaymentErrorHandler)
                .interceptors(loggingInterceptor)
                .rootUri(baseUrl).build();
        restTemplate.setRequestFactory(getClientHttpRequestFactory());
        return new TossPaymentRestTemplate(restTemplate);
    }

    ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleHttpRequestFactory.setReadTimeout(Duration.ofSeconds(readTimeout));
        simpleHttpRequestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeout));
        return simpleHttpRequestFactory;
    }
}
