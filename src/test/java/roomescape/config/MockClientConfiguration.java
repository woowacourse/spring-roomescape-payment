package roomescape.config;

import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.reservation.config.TossClientErrorHandler;
import roomescape.reservation.config.TossServerErrorHandler;

@Configuration
public class MockClientConfiguration {

    private final TossServerErrorHandler tossServerErrorHandler;
    private final TossClientErrorHandler tossClientErrorHandler;

    public MockClientConfiguration(TossServerErrorHandler tossServerErrorHandler,
                                   TossClientErrorHandler tossClientErrorHandler) {
        this.tossServerErrorHandler = tossServerErrorHandler;
        this.tossClientErrorHandler = tossClientErrorHandler;
    }

    @Bean
    public MockServerRestClientCustomizer mockServerRestClientCustomizer() {
        return new MockServerRestClientCustomizer();
    }

    @Bean
    public RestClient restClient(final MockServerRestClientCustomizer customizer) {
        RestClient.Builder builder = RestClient.builder();
        customizer.customize(builder);
        return builder
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultStatusHandler(tossServerErrorHandler)
                .defaultStatusHandler(tossClientErrorHandler)

                .build();
    }

    @Bean
    public MockRestServiceServer mockRestServiceServer(final MockServerRestClientCustomizer customizer) {
        return customizer.getServer();
    }
}
