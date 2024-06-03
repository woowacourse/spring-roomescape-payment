package roomescape.client;

import org.springframework.context.annotation.Bean;

import feign.codec.ErrorDecoder;

public class PaymentFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PaymentErrorDecoder();
    }
}
