package roomescape.config;

import org.apache.logging.log4j.util.Base64Util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    private final static String PAYMENT_URL = "https://api.tosspayments.com/v1/payments";
    private final static String AUTH_TYPE = "Basic ";
    private final static String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    @Bean
    RestClient paymentRestClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, buildAuthHeader())
                .baseUrl(PAYMENT_URL).build();
    }

    private String buildAuthHeader() {
        return AUTH_TYPE + Base64Util.encode(SECRET_KEY + ":");
    }
}
