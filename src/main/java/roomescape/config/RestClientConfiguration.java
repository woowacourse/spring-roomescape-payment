package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.service.PaymentClientService;
import roomescape.util.AuthorizationHeaderProvider;

@Configuration
public class RestClientConfiguration {

    private final String baseUrl;
    private final String tokenValue;
    private final AuthorizationHeaderProvider authProvider;

    public RestClientConfiguration(@Value("${toss.base-url}") String baseUrl,
                                   @Value("${toss.secret-key}") String tokenValue,
                                   AuthorizationHeaderProvider authProvider) {
        this.baseUrl = baseUrl;
        this.tokenValue = tokenValue;
        this.authProvider = authProvider;
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        var clientFactory = new HttpComponentsClientHttpRequestFactory();
        clientFactory.setConnectTimeout(1_200);
        clientFactory.setReadTimeout(6_000);

        String authorizationHeader = authProvider.provide(tokenValue);

        return RestClient.builder()
                .requestFactory(clientFactory)
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", authorizationHeader);
    }

    @Bean
    public RestClient restClient() {
        Builder builder = restClientBuilder();
        return builder.build();
    }

    @Bean
    public PaymentClientService createPaymentService() {
        RestClient restClient = restClient();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(PaymentClientService.class);
    }
}
