package roomescape.config;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import roomescape.exception.TossPaymentErrorHandler;
import roomescape.interceptor.LoggingInterceptor;
import roomescape.service.httpclient.TossPaymentClient;
import roomescape.service.httpclient.TossPaymentRestTemplate;

@Configuration
public class TossPaymentConfiguration {
    private final static String API_BASE_URL = "https://api.tosspayments.com/v1/payments";
    private final static String AUTH_TYPE = "Basic ";
    private final static String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final TossPaymentErrorHandler tossPaymentErrorHandler;
    private final LoggingInterceptor loggingInterceptor;

    public TossPaymentConfiguration(final TossPaymentErrorHandler tossPaymentErrorHandler, final LoggingInterceptor loggingInterceptor) {
        this.tossPaymentErrorHandler = tossPaymentErrorHandler;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Bean
    TossPaymentClient tossPaymentClient() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, buildAuthHeader())
                .errorHandler(tossPaymentErrorHandler)
                .interceptors(loggingInterceptor)
                .rootUri(API_BASE_URL).build();
        return new TossPaymentRestTemplate(restTemplate);
    }

    private String buildAuthHeader() {
        return AUTH_TYPE + Base64Util.encode(SECRET_KEY + ":");
    }
}
