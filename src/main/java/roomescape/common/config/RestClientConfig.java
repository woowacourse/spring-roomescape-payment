package roomescape.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("tossRestClientBuilder")
    public Builder tossRestClientBuilder(TossPaymentsProperties tossPaymentsProperties) {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(tossPaymentsProperties.getConnectionTimeout());
        factory.setReadTimeout(tossPaymentsProperties.getReadTimeout());
        return RestClient.builder().requestFactory(factory);
    }
}
