package roomescape.web.controller.api.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientHttpRequestConfig {

    private static final int TIMEOUT = 5000;

    @Bean
    protected ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory  = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(TIMEOUT);
        return clientHttpRequestFactory;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create(new RestTemplate(clientHttpRequestFactory()));
    }
}

