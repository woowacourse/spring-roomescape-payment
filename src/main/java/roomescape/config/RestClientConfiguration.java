package roomescape.config;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    private final static String AUTH_TYPE = "Basic ";
    @Value("${payment.toss.base-url}")
    private String TossPaymentBaseUrl;
    @Value("${payment.toss.payment-url}")
    private String TossPaymentUrl;
    @Value("${payment.toss.secret-key}")
    private String secretKey;

    @Bean
    RestClient paymentRestClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, buildAuthHeader())
                .baseUrl(TossPaymentBaseUrl + TossPaymentUrl).build();
    }

    private String buildAuthHeader() {
        return AUTH_TYPE + Base64Util.encode(secretKey + ":");
    }
}
