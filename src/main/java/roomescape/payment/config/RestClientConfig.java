package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.global.auth.util.AuthUtil;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class RestClientConfig {

    private final PaymentProperties paymentProperties;

    public RestClientConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public Map<String, RestClient> builders() {
        Map<String, RestClient> clients = new HashMap<>();

        paymentProperties.getProperties().forEach(
                (vendorName, vendor) -> {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(vendor.getConnectTimeout());
                    factory.setReadTimeout(vendor.getReadTimeout());
                    RestClient client = RestClient.builder()
                            .requestFactory(factory)
                            .baseUrl(vendor.getBaseUrl())
                            .defaultHeader(HttpHeaders.AUTHORIZATION, AuthUtil.encodeBasicAuth(vendor.getSecretKey()))
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                            .build();
                    clients.put(vendorName, client);
                }
        );
        return clients;
    }

    @Bean(name = "tossRestClient")
    public RestClient tossRestClient(Map<String, RestClient> clients) {
        return clients.get("toss");
    }
}
