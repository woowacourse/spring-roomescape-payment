package roomescape.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import roomescape.infrastructure.thirdparty.filter.TossPaymentErrorResponseFilter;

@Configuration
public class RestTemplateConfig {

    @Bean("TossRestTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public TossPaymentErrorResponseFilter tossPaymentErrorResponseFilter(ObjectMapper objectMapper) {
        return new TossPaymentErrorResponseFilter(objectMapper);
    }
} 
