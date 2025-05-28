package roomescape.payment.client;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import roomescape.common.util.TokenCookieManager;

import java.beans.BeanProperty;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@TestConfiguration
public class TossPaymentTestConfig {

    @Bean
    @Primary
    public RestClient tossClient(){
        String secretKey = "WRONG_SECRET_KEY";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public TokenCookieManager tokenCookieManager() {
        return new TokenCookieManager(); // 생성자 인자가 있다면 채워야 함
    }
}
