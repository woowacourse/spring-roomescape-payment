package roomescape.client;

import feign.Client;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PaymentErrorDecoder();
    }

    @Bean
    Client client(){
        return new OkHttpClient();
    }
}
