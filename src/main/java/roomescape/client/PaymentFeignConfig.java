package roomescape.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;

@Configuration
public class PaymentFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PaymentErrorDecoder();
    }

    @Bean
    Client client() {
        return new OkHttpClient();
    }
}
